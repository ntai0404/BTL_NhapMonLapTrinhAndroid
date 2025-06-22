package org.meicode.project2272.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;

import org.meicode.project2272.Model.BannerModel;
import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.Model.CategoryModel;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.UserModel;
import org.meicode.project2272.Respository.MainRespository;

import java.util.ArrayList;

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
    public MutableLiveData<ArrayList<BillModel>> getAllOrders() {
        return respository.getAllOrders();
    }

    public MutableLiveData<ArrayList<BillModel>> getUserOrders(String userId) {
        return respository.getUserOrders(userId);
    }
    public void cancelOrder(BillModel bill) {
        respository.cancelOrder(bill);
    }
}

