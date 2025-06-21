package org.meicode.project2272.ViewModel;

import androidx.lifecycle.MediatorLiveData;
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

    public void addProduct(ItemsModel item) {
        respository.addProduct(item);
    }

    public void updateProduct(ItemsModel item) {
        respository.updateProduct(item);
    }

    public Task<Void> deleteProduct(String itemId) {
        return respository.deleteProduct(itemId);
    }

    public LiveData<ArrayList<BillModel>> loadAllBills() {
        return respository.loadAllBills();
    }

    public LiveData<ArrayList<BillModel>> loadPendingBills() {
        return respository.loadPendingBills();
    }
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

    // 1. Sửa hàm getCartItems để nhận userId
    public LiveData<ArrayList<ItemsModel>> getCartItems(String userId) {
        MediatorLiveData<ArrayList<ItemsModel>> combinedData = new MediatorLiveData<>();
        LiveData<ArrayList<ItemsModel>> allItemsLiveData = respository.getAllItems();
        LiveData<Map<String, Long>> cartLiveData = respository.getCart(userId);

        Runnable updateCombinedData = () -> {
            ArrayList<ItemsModel> allItems = allItemsLiveData.getValue();
            Map<String, Long> cartMap = cartLiveData.getValue();

            if (allItems == null || cartMap == null) {
                return;
            }

            ArrayList<ItemsModel> cartItemsList = new ArrayList<>();
            for (ItemsModel item : allItems) {
                if (cartMap.containsKey(item.getId())) {
                    ItemsModel itemInCart = new ItemsModel();
                    itemInCart.setId(item.getId());
                    itemInCart.setTitle(item.getTitle());
                    itemInCart.setPrice(item.getPrice());
                    itemInCart.setPicUrl(item.getPicUrl());
                    itemInCart.setNumberinCart(cartMap.get(item.getId()).intValue());
                    cartItemsList.add(itemInCart);
                }
            }
            combinedData.setValue(cartItemsList);
        };

        combinedData.addSource(allItemsLiveData, allItems -> updateCombinedData.run());
        combinedData.addSource(cartLiveData, cartMap -> updateCombinedData.run());

        return combinedData;
    }

    // 2. Sửa hàm manageCartItem để nhận userId
    public void manageCartItem(String userId, String itemId, int change) {
        respository.manageCartItem(userId, itemId, change);
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

}

