package org.meicode.project2272.Respository;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.meicode.project2272.Model.BannerModel;
import org.meicode.project2272.Model.CategoryModel;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.UserModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

public class MainRespository {
    private final FirebaseDatabase  firebaseDatabase= FirebaseDatabase.getInstance();

    //Login 4 User >.<
    //load User uses the same way >.<
    public LiveData<ArrayList<UserModel>> loadUser(String nameOrEmail, String password, String temp){
        MutableLiveData<ArrayList<UserModel>> listData=new MutableLiveData<>();
        DatabaseReference ref=firebaseDatabase.getReference("User");
        ref.orderByChild(temp).equalTo(nameOrEmail).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                ArrayList<UserModel> list = new ArrayList<>();
                if(snapshot!= null) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        UserModel item = childSnapshot.getValue(UserModel.class);
                        if (item != null) list.add(item);
                    }
                    listData.setValue(list);
                }
                else{listData.postValue(null);};
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
            }
        });return listData;
    }

    //Register 4 User >.<

    //Load Category
    public LiveData<ArrayList<CategoryModel>> loadCategory(){
        MutableLiveData<ArrayList<CategoryModel>> listData=new MutableLiveData<>();
        DatabaseReference ref=firebaseDatabase.getReference("Category");
        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                ArrayList<CategoryModel> list = new ArrayList<>();
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    CategoryModel item = childSnapshot.getValue(CategoryModel.class);
                    if(item !=null) list.add(item);
                }
                listData.setValue(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){

            }
        });return listData;
    }

    //Banner
    public LiveData<ArrayList<BannerModel>> loadBanner(){
        MutableLiveData<ArrayList<BannerModel>> listData=new MutableLiveData<>();
        DatabaseReference ref=firebaseDatabase.getReference("Banner");
        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                ArrayList<BannerModel> list = new ArrayList<>();
                for(DataSnapshot childSnapshot : snapshot.getChildren()){
                    BannerModel item = childSnapshot.getValue(BannerModel.class);
                    if(item !=null) list.add(item);
                }
                listData.setValue(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){

            }
        });return listData;
    }

    //Items Popular
// Phương thức trả về dữ liệu dạng LiveData để có thể quan sát được từ UI
    public LiveData<ArrayList<ItemsModel>> loadPopular() {
        // Tạo một MutableLiveData để chứa danh sách các ItemsModel
        MutableLiveData<ArrayList<ItemsModel>> listData = new MutableLiveData<>();

        // Tham chiếu tới node "Items" trong Firebase Realtime Database
        DatabaseReference ref = firebaseDatabase.getReference("Items");

        // Lắng nghe dữ liệu từ node "Items"
        ref.addValueEventListener(new ValueEventListener() {

            // Khi dữ liệu thay đổi hoặc lần đầu load
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Tạo danh sách để chứa các đối tượng ItemsModel
                ArrayList<ItemsModel> list = new ArrayList<>();

                // Duyệt qua tất cả các phần tử con trong snapshot
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Chuyển đổi dữ liệu snapshot con thành đối tượng ItemsModel
                    ItemsModel item = childSnapshot.getValue(ItemsModel.class);
                    // Nếu item không null thì thêm vào danh sách
                    if (item != null) list.add(item);
                }

                // Gán danh sách vào LiveData để cập nhật cho UI
                listData.setValue(list);
            }

            // Hàm xử lý khi có lỗi truy cập dữ liệu từ Firebase
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // nahhhhhhhh >.<
            }
        });
        // Trả về đối tượng LiveData để UI có thể quan sát dữ liệu
        return listData;
    }



}
