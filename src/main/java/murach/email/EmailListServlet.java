package murach.email;

import jakarta.persistence.PersistenceException;
import murach.business.User;
import murach.data.UserDAO;
import murach.util.MailUtilLocal;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/emailList")
public class EmailListServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String firstName = readParameter(request, "firstName");
        String lastName = readParameter(request, "lastName");
        String email = readParameter(request, "email");

        User user = new User(firstName, lastName, email);

        request.setAttribute("user", user);

        String url = "/index.jsp";
        String message = null;

        if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()) {

            message = "Vui lòng nhập đầy đủ thông tin.";

        } else if (firstName.length() > 50
                || lastName.length() > 50
                || email.length() > 255) {

            message = "Họ và tên tối đa 50 ký tự; email tối đa 255 ký tự.";

        } else if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            message = "Địa chỉ email không hợp lệ.";

        } else {
            try {
                if (UserDAO.emailExists(email)) {

                    message = "Email này đã tồn tại. "
                            + "Vui lòng nhập email khác.";

                } else {
                    int rows = UserDAO.insert(user);

                    if (rows == 1) {
                        sendWelcomeEmail(request, user);
                        url = "/thanks.jsp";
                    } else {
                        message = "Chưa lưu được thông tin. "
                                + "Vui lòng thử lại.";
                    }
                }

            } catch (PersistenceException e) {
                getServletContext().log(
                        "Khong the dang ky Email List", e
                );

                message = "Không thể lưu dữ liệu. "
                        + "Vui lòng kiểm tra kết nối database.";
            }
        }

        request.setAttribute("message", message);

        request.getRequestDispatcher(url)
                .forward(request, response);
    }

    private String readParameter(
            HttpServletRequest request,
            String name
    ) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }

    private void sendWelcomeEmail(
            HttpServletRequest request,
            User user
    ) {
        String to = user.getEmail();
        String from = System.getenv("SMTP_USERNAME");
        String subject = "Welcome to our email list";
        String body = "Dear " + user.getFirstName() + ",\n\n"
                + "Thanks for joining our email list. "
                + "We'll make sure to send you announcements about "
                + "new products and promotions.\n\n"
                + "Have a great day and thanks again!\n\n"
                + "Mike Murach & Associates";

        try {
            MailUtilLocal.sendMail(
                    to, from, subject, body, false
            );
        } catch (MessagingException e) {
            request.setAttribute(
                    "mailWarning",
                    "Đăng ký đã được lưu nhưng chưa gửi được email chào mừng."
            );

            getServletContext().log(
                    "Khong the gui email chao mung den " + to, e
            );
        }
    }
}
