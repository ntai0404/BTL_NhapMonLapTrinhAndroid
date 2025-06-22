package org.meicode.project2272.Respository;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.meicode.project2272.Model.BannerModel;
import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.Model.CategoryModel;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.NotificationModel;
import org.meicode.project2272.Model.UserModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

public class MainRespository {
    private final FirebaseDatabase  firebaseDatabase= FirebaseDatabase.getInstance();

    //Login 4 User >.<
    public LiveData<ArrayList<UserModel>> loadUser(String nameOrEmail, String password, String temp){
        MutableLiveData<ArrayList<UserModel>> listData=new MutableLiveData<>();
        DatabaseReference ref=firebaseDatabase.getReference("User");
        ref.orderByChild(temp).equalTo(nameOrEmail).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                ArrayList<UserModel> list = new ArrayList<>();
                if(snapshot.exists()) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        UserModel item = childSnapshot.getValue(UserModel.class);
                        if (item != null && item.getPassword().equals(password)) {
                            list.add(item);
                        }
                    }
                    listData.setValue(list);
                }
                else{
                    listData.postValue(null);
                };
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
            }
        });
        return listData;
    }

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
        });
        return listData;
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
        });
        return listData;
    }

    //Items Popular
    public LiveData<ArrayList<ItemsModel>> loadPopular() {
        MutableLiveData<ArrayList<ItemsModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Items");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ItemsModel> list = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    ItemsModel item = childSnapshot.getValue(ItemsModel.class);
                    if (item != null) {
                        list.add(item);
                    }
                }
                listData.setValue(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // nahhhhhhhh >.<
            }
        });
        return listData;
    }


    // --- LOGIC CHO GIỎ HÀNG (CART) ĐÃ SỬA ĐỔI ---

    public LiveData<ArrayList<ItemsModel>> getAllItems() {
        return loadPopular();
    }

    // 1. Sửa hàm manageCartItem để nhận userId
    public void manageCartItem(String userId, String itemId, int change) {
        if (userId == null || itemId == null) return;

        DatabaseReference cartItemRef = firebaseDatabase.getReference("Carts")
                .child(userId)
                .child(itemId);

        cartItemRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Long currentQuantity = mutableData.getValue(Long.class);
                if (currentQuantity == null) {
                    if (change > 0) mutableData.setValue(change);
                } else {
                    long newQuantity = currentQuantity + change;
                    if (newQuantity > 0) {
                        mutableData.setValue(newQuantity);
                    } else {
                        mutableData.setValue(null); // Xóa item nếu số lượng <= 0
                    }
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (error != null) System.out.println("Firebase transaction failed: " + error.getMessage());
            }
        });
    }

    // 2. Sửa hàm getCart để nhận userId
    public MutableLiveData<Map<String, Long>> getCart(String userId) {
        MutableLiveData<Map<String, Long>> cartData = new MutableLiveData<>();
        if (userId == null) {
            cartData.postValue(new HashMap<>()); // Trả về giỏ hàng rỗng nếu không có userId
            return cartData;
        }

        DatabaseReference cartRef = firebaseDatabase.getReference("Carts").child(userId);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Long> cartMap = new HashMap<>();
                if (snapshot.exists()) {
                    for(DataSnapshot itemSnapshot : snapshot.getChildren()){
                        String itemId = itemSnapshot.getKey();
                        Long quantity = itemSnapshot.getValue(Long.class);
                        if(itemId != null && quantity != null){
                            cartMap.put(itemId, quantity);
                        }
                    }
                }
                cartData.postValue(cartMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cartData.postValue(new HashMap<>());
            }
        });
        return cartData;
    }
    // -- end logic CART >.<

    // --- LOGIC CHO Notification ---
    public LiveData<ArrayList<NotificationModel>> loadNotifications(String userId) {
        MutableLiveData<ArrayList<NotificationModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Notifications").child(userId);

        // Sắp xếp để thông báo mới nhất lên đầu (nếu key là push key)
        ref.orderByKey().limitToLast(100).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<NotificationModel> list = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    NotificationModel item = childSnapshot.getValue(NotificationModel.class);
                    if (item != null) {
                        list.add(item);
                    }
                }
                // Đảo ngược danh sách để thông báo mới nhất ở trên cùng
                java.util.Collections.reverse(list);
                listData.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
        return listData;
    }
    // Logic update status Notification
    public void markNotificationAsRead(String userId, String notificationId) {
        if (userId == null || notificationId == null) return;

        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(userId)
                .child(notificationId)
                .child("read"); // Tham chiếu trực tiếp đến trường "read"

        notificationRef.setValue(true); // Cập nhật giá trị
    }
    //End Logic for Notification


    // *** BẮT ĐẦU CẬP NHẬT CHO PROFILE ***
    // THÊM HÀM MỚI ĐỂ CẬP NHẬT THÔNG TIN USER
    public void updateUser(UserModel user) {
        if (user != null && user.getUid() != null) {
            DatabaseReference ref = firebaseDatabase.getReference("User");

            // Vì cấu trúc là Mảng, ta phải tìm đúng 'index' của user dựa trên 'uid'
            Query query = ref.orderByChild("uid").equalTo(user.getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            // Lấy key (là index "0", "1", ...) của user cần cập nhật
                            String userKey = childSnapshot.getKey();
                            if (userKey != null) {
                                // Cập nhật toàn bộ đối tượng user vào đúng vị trí (index) đó
                                ref.child(userKey).setValue(user);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
    }
    /**
     * Hàm mới để lấy thông tin người dùng một lần duy nhất.
     * Sử dụng addListenerForSingleValueEvent để không theo dõi liên tục.
     * @param uid ID của người dùng cần lấy.
     * @param listener Callback để xử lý kết quả.
     */
    public void fetchUserOnce(String uid, ValueEventListener listener) {
        if (uid == null) {
            listener.onCancelled(DatabaseError.fromException(new Exception("User ID is null")));
            return;
        }

        DatabaseReference ref = firebaseDatabase.getReference("User");
        Query query = ref.orderByChild("uid").equalTo(uid);

        query.addListenerForSingleValueEvent(listener);
    }
    // --- KẾT THÚC CẬP NHẬT CHO PROFILE ---
    public MutableLiveData<ArrayList<BillModel>> getAllOrders() {
        MutableLiveData<ArrayList<BillModel>> liveData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Bills");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BillModel> allOrders = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Kiểm tra xem đây là một đơn hàng (có trường userId) hay là một nhánh userId
                    if (childSnapshot.hasChild("userId")) {
                        // Trường hợp 1: Đơn hàng nằm phẳng
                        BillModel bill = childSnapshot.getValue(BillModel.class);
                        if (bill != null) {
                            allOrders.add(bill);
                        }
                    } else {
                        // Trường hợp 2: Đây là nhánh userId, chứa các đơn hàng bên trong
                        for (DataSnapshot billSnapshot : childSnapshot.getChildren()) {
                            BillModel bill = billSnapshot.getValue(BillModel.class);
                            if (bill != null) {
                                allOrders.add(bill);
                            }
                        }
                    }
                }

                // Sắp xếp tất cả hóa đơn theo ngày tạo
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    allOrders.sort(Comparator.comparing(BillModel::getCreatedAt).reversed());
                } else {
                    Collections.sort(allOrders, (o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
                }
                liveData.setValue(allOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
        return liveData;
    }
    // --- END LOGIC CHO ORDERS ---
    public MutableLiveData<ArrayList<BillModel>> getUserOrders(String userId) {
        MutableLiveData<ArrayList<BillModel>> liveData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Bills");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BillModel> userOrders = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // Kiểm tra xem có phải là nhánh userId của người dùng hiện tại không
                    if (childSnapshot.getKey().equals(userId)) {
                        for (DataSnapshot billSnapshot : childSnapshot.getChildren()) {
                            BillModel bill = billSnapshot.getValue(BillModel.class);
                            if (bill != null) {
                                userOrders.add(bill);
                            }
                        }
                    }
                    // Kiểm tra xem có phải là đơn hàng phẳng của người dùng hiện tại không
                    else if (childSnapshot.hasChild("userId") && childSnapshot.child("userId").getValue(String.class).equals(userId)) {
                        BillModel bill = childSnapshot.getValue(BillModel.class);
                        if (bill != null) {
                            userOrders.add(bill);
                        }
                    }
                }

                // Sắp xếp các hóa đơn theo ngày tạo
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    userOrders.sort(Comparator.comparing(BillModel::getCreatedAt).reversed());
                } else {
                    Collections.sort(userOrders, (o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
                }
                liveData.setValue(userOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
        return liveData;
    }
    public void cancelOrder(BillModel billToCancel) {
        if (billToCancel == null || billToCancel.getBillId() == null || billToCancel.getUserId() == null) {
            return; // Không đủ thông tin để hủy
        }

        String userId = billToCancel.getUserId();
        String billId = billToCancel.getBillId();

        DatabaseReference ref = firebaseDatabase.getReference("Bills");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Thử tìm trong cấu trúc lồng nhau trước
                if (snapshot.hasChild(userId) && snapshot.child(userId).hasChild(billId)) {
                    snapshot.child(userId).child(billId).getRef().child("status").setValue("Đã hủy");
                }
                // Nếu không thấy, thử tìm trong cấu trúc phẳng
                else if (snapshot.hasChild(billId)) {
                    snapshot.child(billId).getRef().child("status").setValue("Đã hủy");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi tìm đơn hàng để hủy
            }
        });
    }


}