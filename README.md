# Deploy First Web - JPA/JPQL

Phiên bản JPA của project Email List. Giao diện Servlet/JSP được giữ nguyên,
trong khi tầng truy cập dữ liệu sử dụng Jakarta Persistence, Hibernate và JPQL
thay cho JDBC viết tay.

## Công nghệ

- Java 17 trở lên (đã kiểm tra với Java 25)
- Servlet API 4.0 và Tomcat 9
- Jakarta Persistence 3.2
- Hibernate ORM 7.4
- MySQL Connector/J 8.4
- Maven WAR project

## Cấu trúc JPA chính

- `murach.business.User`: entity ánh xạ bảng `User`.
- `murach.connection.JpaUtil`: tạo một `EntityManagerFactory` dùng chung.
- `murach.connection.JpaShutdownListener`: đóng factory khi ứng dụng dừng.
- `murach.data.UserDAO`: JPQL kiểm tra email và transaction để insert.
- `src/main/resources/META-INF/persistence.xml`: persistence unit và kết nối MySQL.

## Database

Không lưu thông tin database thật trong source code. Khi deploy, cấu hình các
biến môi trường sau:

```text
DB_URL=jdbc:mysql://database-host:3306/database-name?sslmode=require
DB_USERNAME=database-username
DB_PASSWORD=database-password
```

Nếu không có các biến này, project dùng cấu hình local trong
`src/main/resources/META-INF/persistence.xml`. File `.env.example` liệt kê các
biến cần thiết nhưng ứng dụng không tự đọc file `.env`; hãy đặt chúng trong cấu
hình Tomcat, Docker hoặc nền tảng deploy.

File `database/schema.sql` chứa schema tối thiểu cho một database mới.

`jakarta.persistence.schema-generation.database.action` được đặt thành `none`,
vì vậy Hibernate không tự tạo, sửa hoặc xóa bảng.

## Build

```shell
mvn clean package
```

WAR được tạo tại:

```text
target/FirstWebJPA.war
```

Có thể deploy WAR lên Tomcat 9 hoặc chạy bằng Dockerfile của project.

## Luồng xử lý

```text
EmailListServlet
    -> UserDAO
        -> EntityManager
            -> Hibernate
                -> JDBC
                    -> MySQL
```

JPQL kiểm tra email:

```jpql
SELECT COUNT(u) FROM User u WHERE u.email = :email
```

`User` và `email` trong câu trên là entity và field Java, không phải tên bảng
và tên cột SQL.
