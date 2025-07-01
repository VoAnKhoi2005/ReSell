
# ReSell

Ứng dụng đồ án cho môn SE114

| **MSSV** |  **Họ và tên**  |
|:--------:|:---------------:|
| 23520790 |    Võ An Khôi   |
| 23520905 |  Võ Hồng Lương  |
| 23520819 | Quách Gia Kiệt  |
| 23520887 | Phạm Thành Long |

  
<br>  
<br>  

# Hướng dẫn chạy source code
1.  Vào thư mục project:
```
cd ReSell
```

2. Chạy backend  + admin web 
   
```
docker compose up --build 
```
>[!NOTE]
> Phải đảm bảo các yêu cầu sau: 
>  - Docker engine đang chạy
> - Có file .env và firebase_secret.json trong project


3. Copy public url của backend (ở dòng cuối của log khi chạy docker compose), có dạng https://xxx.ngrok-free.app 
   
<br>
   
![Backend Public URL](images/backend_url.png)

<br>

4. Vào Android Studio, mở file local.properties (trong folder Grade Scripts nếu chọn cách hiển thị Android), sau đó thêm dòng sau: 
```
BASE_URL=https://xxx.ngrok-free.app/api/
``` 
![Android Base URL](images/android_base_url.png)

>[!NOTE]
> **Lưu ý:** Nhớ dùng url copy từ log


5. Build app Android là xài được  


<br>  
<br>

# Danh sách tính năng
## 1. Quản trị viên (Admin)

- Quản lý danh mục sản phẩm (ID, tên danh mục)
- Duyệt/bỏ duyệt bài đăng (lọc hàng giả, hàng cấm)
- Duyệt hồ sơ tài khoản (chống lừa đảo, giả mạo)
- Quản lý khiếu nại và báo cáo từ người dùng
- Thống kê hoạt động toàn hệ thống

## 2. Người dùng

### 2.1. Tài khoản và hồ sơ

- Đăng ký, đăng nhập bằng email/số điện thoại
- Quản lý hồ sơ cá nhân
- Xác minh tài khoản qua email và số điện thoại
- Một tài khoản có thể vừa mua vừa bán
- Quản lý đơn mua và đơn bán
- Báo cáo sản phẩm hoặc người dùng vi phạm
- Thống kê cá nhân: số lượt xem, đơn hàng, doanh thu, điểm uy tín

### 2.2. Người bán

- Đăng, sửa, xóa bài rao bán sản phẩm
  - Thông tin gồm: tiêu đề, mô tả, ảnh, giá, danh mục, tình trạng, vị trí,...
- Tạo khuyến mãi/ưu đãi
- Trò chuyện với người mua
- Giao hàng và cập nhật trạng thái đơn hàng
- Nhận tiền khi đơn hoàn tất
- Xem thống kê: lượt xem, lượt theo dõi, điểm uy tín

### 2.3. Người mua

- Tìm kiếm, duyệt, sắp xếp và lọc tin rao bán theo:
  - Danh mục, khu vực, tầm giá, thời gian đăng
- Xem thông tin sản phẩm và người bán
- Thêm vào giỏ hàng
- Liên hệ với người bán (nhắn tin, gọi điện)
- Đặt hàng với lựa chọn nhiều địa chỉ giao hàng (1 mặc định)
- Theo dõi trạng thái đơn hàng
- Thanh toán bằng tiền mặt hoặc ví trung gian
- Theo dõi người bán (nhận thông báo khi có bài đăng mới)
- Đánh giá giao dịch


## 3. Nhắn tin & Tương tác

- Nhắn tin giữa người mua và người bán
- Gửi tin nhắn, hình ảnh
- Hỗ trợ trả giá trực tiếp trong chat
- Gửi thông báo đẩy khi có tin nhắn mới


## 4. Thanh toán & Giao dịch

- Thanh toán trung gian (escrow): giữ tiền đến khi giao hàng thành công
- Hỗ trợ tiền mặt hoặc ví nội bộ
- Lịch sử giao dịch và doanh thu người bán


## 5. Bảo mật

- Xác minh email, số điện thoại
- Khóa/báo cáo tài khoản vi phạm
- Kiểm duyệt nội dung tự động hoặc thủ công


## 6. Tính năng khác

- Gợi ý sản phẩm theo hành vi người dùng
- Tài khoản thành viên (membership) với nhiều ưu đãi
- Hệ thống thông báo (đơn hàng, đánh giá, tin mới...)
- Dashboard thống kê riêng cho admin và người dùng

---
