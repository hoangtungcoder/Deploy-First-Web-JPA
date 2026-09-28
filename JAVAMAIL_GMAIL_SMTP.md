# JavaMail, Gmail SMTP và `MailUtilLocal`

## Luồng xử lý

Sau khi người dùng đăng ký, `EmailListServlet` kiểm tra dữ liệu và gọi `UserDAO` để lưu entity `User` bằng JPA. Chỉ khi transaction insert thành công, servlet mới gọi `MailUtilLocal` để gửi email chào mừng.

```text
Trình duyệt
    -> POST /emailList
    -> EmailListServlet kiểm tra dữ liệu
    -> UserDAO persist User và commit transaction
    -> MailUtilLocal tạo MimeMessage
    -> Transport.send() kết nối smtp.gmail.com:587
    -> Gmail SMTP chuyển thư đến người nhận
```

JavaMail là SMTP client, không phải mail server. `Session` giữ cấu hình kết nối, `MimeMessage` biểu diễn nội dung email và `Transport` giao email cho Gmail SMTP.

## Cấu hình Gmail

`MailUtilLocal` đọc thông tin đăng nhập từ hai biến môi trường:

```text
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
```

`SMTP_PASSWORD` phải là Google App Password của tài khoản đã bật xác minh hai bước, không phải mật khẩu Gmail thông thường. Không lưu username hoặc App Password trực tiếp trong source code.

Kết nối dùng:

- host `smtp.gmail.com`;
- cổng `587`;
- SMTP authentication;
- STARTTLS bắt buộc;
- UTF-8 cho subject và body.

Có thể đặt `MAIL_DEBUG=true` để in quá trình trao đổi SMTP vào log Tomcat. Mặc định debug được tắt và code không ghi mật khẩu vào log.

## Quan hệ giữa database và email

Transaction JPA kết thúc trước khi gửi email. Nếu database thất bại, ứng dụng không gửi thư. Nếu database thành công nhưng Gmail SMTP thất bại, bản ghi vẫn tồn tại; servlet ghi exception vào log và `thanks.jsp` hiển thị cảnh báo gửi mail.

Database transaction không thể rollback một email đã gửi và SMTP không tham gia JPA transaction. Với hệ thống thực tế cần bảo đảm gửi lại, có thể dùng outbox hoặc queue để lưu tác vụ gửi email và retry sau.

## Dependency

Project sử dụng Tomcat 9 và `javax.servlet`, vì vậy `pom.xml` thêm JavaMail với package `javax.mail`:

```xml
<dependency>
  <groupId>com.sun.mail</groupId>
  <artifactId>javax.mail</artifactId>
  <version>1.6.2</version>
</dependency>
```

Không trộn import `jakarta.mail` vào helper này. Các entity vẫn sử dụng `jakarta.persistence`; JPA và JavaMail là hai API độc lập.

## Kiểm tra khi chạy

Phải đặt biến môi trường trên chính cấu hình Tomcat đang deploy `Deploy-First-Web-JPA`, rồi restart Tomcat. Khi thử, dùng một địa chỉ chưa tồn tại trong database vì servlet không gửi lại email cho bản ghi trùng.

Sau khi build, artifact cần chứa:

```text
WEB-INF/classes/murach/util/MailUtilLocal.class
WEB-INF/classes/murach/email/EmailListServlet.class
WEB-INF/lib/javax.mail-1.6.2.jar
WEB-INF/lib/activation-1.1.jar
```
