- Tomcat có thể chạy nhiều app trên cùng 1 lúc trên nhiều cổng

- Mỗi một folder trong webapps là source của một app

- Mỗi ứng dụng web được triển khai có một và chỉ một ServletContext.
    + Mỗi một file web.xml sẽ tạo ra một ServletContext

- Mỗi ServletContext sẽ có nhiều servlet, filter...

- Tomcat sẽ sử dụng class loader riêng để tải các class của từng app vào JVM.
    + Thư mục class là nơi chứa các file .class đã được compile từ fie java của app, tomcat sẽ tìm các file .class này
      để load vào JVM
    + Thư mục lib là các thư viện jar được sử dụng trong dự án, Tomcat sẽ giải nén các lib này để load các file .class
      vào JVM

- Thuộc tính "listener-class" được khai báo trong file web.xml là điểm vào duy nhất của spring, kích hoạt các sự kiện để
  khởi tạo spring
    + Tomcat sẽ gọi class ContextLoaderListener và truyển vào một ServletContext
    + Sau khi WebApplicationContext được tạo nó sẽ được lưu vào Attribute của ServletContext
    + Vì được lưu trong ServletContext nên Dispatcher và Filter có thể get WebApplicationContext thông qua
      ServletContext

- "listener-class" sẽ tạo một Root Application Context -> các tham số khời tạo được lấy từ "context-param"

- "dispatcher-servlet" sẽ tạo một Dispatcher Application Context -> các tham số khời tạo được lấy từ "init-param" nằm
  trong "servlet"

+ Dispatcher Context là con của Root Context nên sẽ dùng được các bean được define trong Root
+ Application Context sẽ lưu 1 trường là parent chứa tham chiếu tới Context cha nhờ vậy Context con có thể sử dụng các
  bean của cha
+ Root Context không chứa tham chiếu tới các Context con nên k thể sử dụng các bean được define ở Context con

https://howtodoinjava.com/spring-mvc/contextloaderlistener-vs-dispatcherservlet/





Thread.currentThread().setContextClassLoader(new MyWebAppLoader());
Tomcat set class loader của mình vào currentThread để spring có thể sử dụng

Thread.currentThread().getContextClassLoader()
Spring sử dụng ContextClassLoader để tải các bean

tomcat nhúng cho spring mvc
https://programmer.help/blogs/tomcat9-free-web.xml-embedded-in-spring-mvc.html




Mô hình không đồng bộ Servlet giới thiệu ranh giới không đồng bộ giữa các luồng vùng chứa (container threads)
(1 request / thread Servlet) và việc xử lý request trong ứng dụng của bạn.
Quá trình xử lý có thể xảy ra trên một chuỗi khác hoặc chờ đợi. 
Cuối cùng, bạn phải gửi trở lại luồng vùng chứa và đọc / ghi theo cách chặn 
( InputStream và OutputStream vốn dĩ là các API chặn).
InputStream và OutputStream dọc và ghi tuần tự theo byte nên các tài nguyên phải xếp hàng đợi cho đến khi được đọc/ghi đến hết

Với mô hình đó, bạn cần nhiều luồng để đạt được sự đồng thời (vì nhiều luồng có thể bị chặn khi chờ I / O). Điều này gây tốn kém tài nguyên và nó có thể là một sự đánh đổi, tùy thuộc vào trường hợp sử dụng của bạn.




Vòng đời của Servlet
 Vòng đời Servlet được quản lý bởi các thùng chứa và bao gồm bốn giai đoạn:

Nạp và khởi tạo, thùng chứa chịu trách nhiệm tải và khởi tạo Servlet.
Khởi tạo, vùng chứa gọi phương thức init để khởi tạo đối tượng Servlet và phương thức init chỉ được gọi một lần.
Để xử lý yêu cầu, vùng chứa gọi phương thức dịch vụ để xử lý yêu cầu và dịch vụ gọi phương thức doXxx tương ứng để xử lý yêu cầu.
Việc hủy dịch vụ, khi một cá thể Servlet bị xóa khỏi dịch vụ, sẽ gọi phương thức hủy, phương thức này chỉ được thực thi một lần.





đối với các request get/post có sự giới hạn về kích thước dữ liệu được gửi,
giao thức http không giới hạn kích thước mà sợ giới hạn này nằm ở web server
mỗi web server có triển khai kích thước request của riêng mình
Tomcat: kích thước tối đa của header request là 8kb, của body mặc định là là 2m (các kích thước này có thể thay đổi được thông qua cấu hình)
việc giới hạn kích thước tối đa của request nhằm mục đích tránh các cuộc tấn công DDOS
nếu chấp nhận không giới hạn kich thước của request thì tin tặc có thể gửi rất rất nhiều request có kích thước rất lớn lên đến vài GB kiến hệ thống phải tiêu tốn tài nguyên để xử lý
mặc định tomcat chỉ có thể xử lý 150 request cùng lúc (150 thread), nếu tin tặc gửi hàng nghìn request có kích thước hàng gb thì khiến người dùng khác không thể truy cập được trang web vì toàn bộ tài nguyên đang bị xử dụng bời các requet DDOS kia

Các giới hạn được định cấu hình thích hợp giảm thiểu việc khai thác tràn bộ đệm, ngăn chặn các cuộc tấn công Từ chối Dịch vụ (DoS).

Giới hạn request được bật theo mặc định, các request vượt quá độ dài được chỉ định được giả định là tấn công tràn bộ đệm (buffer overflow). Các giá trị mặc định thường thích hợp, nhưng bạn có thể chọn thay đổi một hoặc nhiều giá trị mặc định trong các điều kiện nhất định.

https://campus.barracuda.com/product/webapplicationfirewall/doc/4259870/configuring-request-limits/

