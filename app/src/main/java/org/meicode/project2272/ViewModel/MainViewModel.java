package org.meicode.project2272.ViewModel;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;

import org.meicode.project2272.Model.BannerModel;
import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.Model.CategoryModel;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.NotificationModel;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.Respository.MainRespository;
import org.meicode.project2272.Model.CartModel;

import java.util.ArrayList;
import java.util.Map;

/**
 * ViewModel giữ dữ liệu và xử lý logic trung gian giữa View (giao diện) và Repository (nguồn dữ liệu).
 * Đảm bảo UI luôn nhận dữ liệu một cách rõ ràng và có thể quan sát bằng LiveData.
 */
public class MainViewModel extends ViewModel {

    // Khởi tạo Repository để lấy dữ liệu từ nguồn (Firebase, API, v.v.)
    private final MainRespository respository = new MainRespository();

    /**
     * Trả về LiveData danh sách danh mục (CategoryModel).
     * View sẽ observe để cập nhật khi dữ liệu thay đổi.
     */
    public LiveData<ArrayList<UserModel>> loadUser(String input,String password,String temp){return respository.loadUser(input, password, temp);}
    public LiveData<ArrayList<CategoryModel>> loadCategory() {
        return respository.loadCategory();
    }

    /**
     * Trả về LiveData danh sách banner (BannerModel) – dùng cho carousel/banner quảng cáo.
     */
    public LiveData<ArrayList<BannerModel>> loadBanner() {
        return respository.loadBanner();
    }

    /**
     * Trả về LiveData danh sách item phổ biến (ItemsModel) – ví dụ: sách mượn nhiều nhất.
     */
    public LiveData<ArrayList<ItemsModel>> loadPopular() {
        return respository.loadPopular();
    }

    // --- CODE GIỎ HÀNG ĐÃ SỬA ĐỔI ---
    // --- LOGIC GIỎ HÀNG MỚI ---
    public LiveData<ArrayList<ItemsModel>> getCartItems(String userId) {
        MediatorLiveData<ArrayList<ItemsModel>> combinedData = new MediatorLiveData<>();

        LiveData<ArrayList<ItemsModel>> allItemsLiveData = respository.getAllItems();
        LiveData<ArrayList<CartModel>> cartLiveData = respository.getCart(userId);

        Runnable updateCombinedData = () -> {
            ArrayList<ItemsModel> allItems = allItemsLiveData.getValue();
            ArrayList<CartModel> cartList = cartLiveData.getValue();

            if (allItems == null || cartList == null) {
                return;
            }

            ArrayList<ItemsModel> cartItemsList = new ArrayList<>();
            for (CartModel cartItem : cartList) {
                for (ItemsModel item : allItems) {
                    if (item.getId() != null && item.getId().equals(cartItem.getItemId())) {
                        ItemsModel itemInCart = new ItemsModel();

                        // Sao chép thuộc tính gốc từ sản phẩm
                        itemInCart.setId(item.getId());
                        itemInCart.setTitle(item.getTitle());
                        itemInCart.setPrice(item.getPrice());
                        itemInCart.setPicUrl(item.getPicUrl());
                        //... sao chép các thuộc tính cần thiết khác nếu có

                        // Bổ sung thông tin riêng của giỏ hàng
                        itemInCart.setCartId(cartItem.getCartId());
                        itemInCart.setNumberinCart(cartItem.getQuantity());
                        itemInCart.setSelectedSize(cartItem.getSize());
                        itemInCart.setSelectedColor(cartItem.getColor());

                        cartItemsList.add(itemInCart);
                        break;
                    }
                }
            }
            combinedData.setValue(cartItemsList);
        };

        combinedData.addSource(allItemsLiveData, allItems -> updateCombinedData.run());
        combinedData.addSource(cartLiveData, cartList -> updateCombinedData.run());

        return combinedData;
    }

    public void addToCart(String userId, String itemId, String size, String color, int quantity) {
        respository.addToCart(userId, itemId, size, color, quantity);
    }
    public void updateCartItemQuantity(String userId, String cartId, int change) {
        respository.updateCartItemQuantity(userId, cartId, change);
    }

    // --- CODE Notification ---
    public LiveData<ArrayList<NotificationModel>> loadNotifications(String userId) {
        return respository.loadNotifications(userId);
    }
    public void markNotificationAsRead(String userId, String notificationId) {
        respository.markNotificationAsRead(userId, notificationId);
    }
    // end code Notification

    // *** BẮT ĐẦU CẬP NHẬT CHO PROFILE ***
    // THÊM HÀM MỚI ĐỂ CẬP NHẬT USER, GỌI XUỐNG REPOSITORY
    public void updateUser(UserModel user) {
        respository.updateUser(user);
    }
    // *** BẮT ĐẦU CẬP NHẬT CHO REAL-TIME USER ***

    /**
     * Hàm mới để gọi xuống Repository.
     */
    public void fetchUserOnce(String uid, ValueEventListener listener) {
        respository.fetchUserOnce(uid, listener);
    }
    // --- KẾT THÚC CẬP NHẬT CHO PROFILE ---


    // *** BẮT ĐẦU CHỨC NĂNG TÌM SẢN PHẨM ***
    // LiveData để quản lý trạng thái tìm kiếm -> ẩn/hiện banner
    private final MutableLiveData<Boolean> _isSearching = new MutableLiveData<>(false);
    public LiveData<Boolean> isSearching() {
        return _isSearching;
    }
    // Hàm này sẽ gọi xuống Repository để tìm kiếm sản phẩm
    public LiveData<ArrayList<ItemsModel>> searchProducts(String keyword) {
        // Cập nhật trạng thái: nếu có từ khóa thì đang tìm kiếm, nếu không thì thôi
        _isSearching.postValue(keyword != null && !keyword.trim().isEmpty());

        // Gọi phương thức tương ứng trong repository
        return respository.searchProducts(keyword);
    }

    // --- LOGIC MỚI CHO ĐẶT HÀNG ---
    public LiveData<Boolean> placeOrderAndCreateNotification(BillModel bill) {
        return respository.placeOrderAndCreateNotification(bill);
    }

    public void clearCart(String userId) {
        respository.clearCart(userId);
    }
    // --- KẾT THÚC LOGIC MỚI ---


    // --- LOGIC CODE TRANG ---
    public void addProduct(ItemsModel item) {
        respository.addProduct(item);
    }
    public void updateProduct(ItemsModel item) {
        respository.updateProduct(item);
    }
    public Task<Void> deleteProduct(String itemId) {
        return respository.deleteProduct(itemId);
    }
    public LiveData<ArrayList<BillModel>> loadPendingBills() {
        return respository.loadPendingBills();
    }
    public LiveData<ArrayList<BillModel>> loadAllBills() {
        return respository.loadAllBills();
    }
    // --- END CODE TRANG ---

    // ---LOGIC CODE NHUNG ---
    public MutableLiveData<ArrayList<BillModel>> getAllOrders() {
        return respository.getAllOrders();
    }

    public MutableLiveData<ArrayList<BillModel>> getUserOrders(String userId) {
        return respository.getUserOrders(userId);
    }
    // THÊM PHƯƠNG THỨC MỚI
    public void updateOrderStatus(String billId, String newStatus) {
        respository.updateOrderStatus(billId, newStatus);
    }

    // Phương thức cancelOrder cũ có thể không cần thiết nữa, nhưng cứ để lại
    public void cancelOrder(BillModel bill) { respository.cancelOrder(bill); }
    // ---END LOGIC CODE NHUNG---
}

