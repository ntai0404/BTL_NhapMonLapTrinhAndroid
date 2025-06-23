package org.meicode.project2272.Respository;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import org.meicode.project2272.Model.BannerModel;
import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.Model.CartModel;
import org.meicode.project2272.Model.CategoryModel;
import org.meicode.project2272.Model.ItemsModel;
import org.meicode.project2272.Model.NotificationModel;
import org.meicode.project2272.Model.UserModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

    //Load Category >.< REFUSE, WE DONT NEED >.<
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
            public void onCancelled(@NonNull DatabaseError error){}
        });return listData;
    }

    //Load Banner  >.<
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
            public void onCancelled(@NonNull DatabaseError error){}
        });
        return listData;
    }

    //Items Popular == All Item
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
                        item.setId(childSnapshot.getKey());// Trang coding
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


    // ---START REGION TRUE NGUYEN XUAN TAI ---
    // --- LOGIC CART ---
    public LiveData<ArrayList<ItemsModel>> getAllItems() {
        // có một hàm load tất cả sản phẩm loadPopular ^^^^
        return loadPopular();
    }

    public LiveData<ArrayList<CartModel>> getCart(String userId) {
        MutableLiveData<ArrayList<CartModel>> liveData = new MutableLiveData<>();
        if (userId == null) {
            liveData.postValue(new ArrayList<>());
            return liveData;
        }

        DatabaseReference cartRef = firebaseDatabase.getReference("Carts").child(userId);
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<CartModel> cartList = new ArrayList<>();
                if (snapshot.exists()) {
                    for(DataSnapshot cartEntrySnapshot : snapshot.getChildren()){
                        CartModel cartItem = cartEntrySnapshot.getValue(CartModel.class);
                        if(cartItem != null){
                            cartItem.setCartId(cartEntrySnapshot.getKey());
                            cartList.add(cartItem);
                        }
                    }
                }
                liveData.postValue(cartList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.postValue(new ArrayList<>());
            }
        });
        return liveData;
    }

    public void addToCart(String userId, String itemId, String size, String color, int quantity) {
        if (userId == null) return;

        DatabaseReference cartRef = firebaseDatabase.getReference("Carts").child(userId);

        // 1. Truy vấn để tìm sản phẩm có cùng itemId, size, và color
        Query query = cartRef.orderByChild("itemId").equalTo(itemId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean itemExists = false;
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    CartModel cartItem = childSnapshot.getValue(CartModel.class);

                    // 2. Nếu tìm thấy sản phẩm, kiểm tra tiếp size và color
                    if (cartItem != null && size.equals(cartItem.getSize()) && color.equals(cartItem.getColor())) {
                        // 3. Đã tồn tại -> Cập nhật số lượng
                        int newQuantity = cartItem.getQuantity() + quantity;
                        childSnapshot.getRef().child("quantity").setValue(newQuantity);
                        itemExists = true;
                        break;
                    }
                }

                // 4. Nếu không tồn tại -> Thêm mục mới vào giỏ hàng
                if (!itemExists) {
                    String cartId = cartRef.push().getKey();
                    if (cartId != null) {
                        CartModel newItem = new CartModel();
                        newItem.setItemId(itemId);
                        newItem.setQuantity(quantity);
                        newItem.setSize(size);
                        newItem.setColor(color);
                        // Không cần set cartId vào object vì key của node đã là cartId
                        cartRef.child(cartId).setValue(newItem);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AddToCartError", "Lỗi khi kiểm tra giỏ hàng: " + error.getMessage());
            }
        });
    }

    public void updateCartItemQuantity(String userId, String cartId, int change) {
        if (userId == null || cartId == null) return;

        DatabaseReference cartItemRef = firebaseDatabase.getReference("Carts")
                .child(userId)
                .child(cartId);

        cartItemRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                CartModel cartItem = mutableData.getValue(CartModel.class);
                if (cartItem == null) {
                    return Transaction.success(mutableData);
                }
                long newQuantity = cartItem.getQuantity() + change;
                if (newQuantity > 0) {
                    cartItem.setQuantity((int) newQuantity);
                    mutableData.setValue(cartItem);
                } else {
                    mutableData.setValue(null);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (error != null) {
                    Log.e("FirebaseTransaction", "Lỗi cập nhật giỏ hàng: " + error.getMessage());
                }
            }
        });
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
    // Hàm tạo thông báo trong app (lưu vào Realtime Database)
    private void createInAppNotification(String userId, String content) {
        if (userId == null) return;
        DatabaseReference notifRef = firebaseDatabase.getReference("Notifications").child(userId);
        String notificationId = notifRef.push().getKey();
        if (notificationId == null) return;

        NotificationModel notification = new NotificationModel();
        notification.setNotificationId(notificationId);
        notification.setUserId(userId);
        notification.setContent(content);
        notification.setRead(false);

        notifRef.child(notificationId).setValue(notification);
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
                    // Xử lý lỗi nếu cần >.<
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

    // *** BẮT ĐẦU LOGIC LỌC ***
    // Hàm tiện ích để loại bỏ dấu Tiếng Việt và chuyển thành chữ thường
    private String removeDiacritics(String str) {
        if (str == null) {
            return "";
        }
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        // Thay thế các ký tự dấu, chuyển sang chữ thường và xử lý chữ 'đ'
        return pattern.matcher(nfdNormalizedString).replaceAll("")
                .toLowerCase()
                .replaceAll("đ", "d");
    }

    // *** BẮT ĐẦU PHIÊN BẢN TỐI ƯU LOGIC LỌC ***
    public LiveData<ArrayList<ItemsModel>> searchProducts(String keyword) {
        MutableLiveData<ArrayList<ItemsModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Items");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ItemsModel> fullList = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    ItemsModel item = childSnapshot.getValue(ItemsModel.class);
                    if (item != null) {
                        fullList.add(item);
                    }
                }

                // Nếu không có từ khóa hoặc từ khóa trống, trả về danh sách đầy đủ
                if (keyword == null || keyword.trim().isEmpty()) {
                    listData.postValue(fullList);
                    return;
                }

                // --- Bắt đầu logic lọc thông minh ---
                ArrayList<ItemsModel> filteredList = new ArrayList<>();

                // 1. Chuẩn hóa và tách các từ trong chuỗi tìm kiếm của người dùng
                String normalizedKeyword = removeDiacritics(keyword);
                // Tách chuỗi thành mảng các từ, dựa trên một hoặc nhiều khoảng trắng
                String[] searchKeywords = normalizedKeyword.split("\\s+");

                // Lặp qua toàn bộ sản phẩm để lọc
                for (ItemsModel item : fullList) {
                    if (item.getTitle() == null) {
                        continue; // Bỏ qua sản phẩm không có tên
                    }

                    // 2. Chuẩn hóa tên sản phẩm để so sánh
                    String normalizedTitle = removeDiacritics(item.getTitle());

                    boolean allKeywordsMatch = true; // Giả định sản phẩm này khớp với tất cả từ khóa

                    // 3. Lặp qua từng từ khóa người dùng nhập vào
                    for (String key : searchKeywords) {
                        // Nếu tên sản phẩm đã chuẩn hóa KHÔNG chứa một từ khóa nào đó...
                        if (!normalizedTitle.contains(key)) {
                            allKeywordsMatch = false; // ...thì đánh dấu là không khớp
                            break; // ...và thoát vòng lặp, không cần kiểm tra các từ khóa còn lại cho sản phẩm này
                        }
                    }

                    // 4. Nếu sau khi kiểm tra, sản phẩm vẫn được đánh dấu là khớp tất cả...
                    if (allKeywordsMatch) {
                        filteredList.add(item); // ...thì thêm nó vào danh sách kết quả
                    }
                }
                listData.postValue(filteredList);
                // --- Kết thúc logic lọc thông minh ---
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Ghi log lỗi để dễ dàng gỡ rối khi có sự cố
                Log.e("MainRepository", "Lỗi khi tìm kiếm sản phẩm: " + error.getMessage());
                // Cân nhắc postValue một danh sách rỗng hoặc null để UI có thể xử lý trường hợp lỗi
                listData.postValue(new ArrayList<>());
            }
        });

        return listData;
    }
    // --- KẾT THÚC PHIÊN BẢN ĐÃ TỐI ƯU ---

    // --- LOGIC CHO ĐẶT HÀNG ---
    public LiveData<Boolean> placeOrderAndCreateNotification(BillModel bill) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        DatabaseReference billsRef = firebaseDatabase.getReference("Bills");
        String billId = billsRef.push().getKey();

        if (billId == null) {
            result.postValue(false);
            return result;
        }

        bill.setBillId(billId);
        billsRef.child(billId).setValue(bill).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Tạo thông báo trong app sau khi tạo bill thành công
                String notificationContent = "Đơn hàng #" + billId.substring(Math.max(0, billId.length() - 6)) + " của bạn đã được tạo thành công.";
                createInAppNotification(bill.getUserId(), notificationContent);
                result.postValue(true);
            } else {
                result.postValue(false);
            }
        });

        return result;
    }

    public void clearCart(String userId) {
        if (userId == null) return;
        DatabaseReference cartRef = firebaseDatabase.getReference("Carts").child(userId);
        cartRef.removeValue();
    }
    // --- KẾT THÚC LOGIC MỚI ---

    // --- LOGIC CODE TRANG ---
    public void addProduct(ItemsModel item) {
        DatabaseReference ref = firebaseDatabase.getReference("Items");
        String itemId = ref.push().getKey(); // Tạo ID duy nhất
        if (itemId != null) {
            item.setId(itemId);
            ref.child(itemId).setValue(item); // Lưu sản phẩm với ID mới
        }
    }

    // Cập nhật sản phẩm dựa trên ID
    public void updateProduct(ItemsModel item) {
        if (item != null && item.getId() != null) {
            DatabaseReference ref = firebaseDatabase.getReference("Items").child(item.getId());
            ref.setValue(item);
        }
    }

    // Xóa sản phẩm dựa trên ID
    public Task<Void> deleteProduct(String itemId) {
        DatabaseReference ref = firebaseDatabase.getReference("Items").child(itemId);
        return ref.removeValue();
    }

    public LiveData<ArrayList<BillModel>> loadPendingBills() {
        MutableLiveData<ArrayList<BillModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Bills");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BillModel> pendingList = new ArrayList<>();
                // SỬA LỖI: Đơn giản hóa vòng lặp cho cấu trúc phẳng "Bills -> billId"
                for (DataSnapshot billSnapshot : snapshot.getChildren()) {
                    try {
                        BillModel item = billSnapshot.getValue(BillModel.class);
                        if (item != null && "Pending".equalsIgnoreCase(item.getStatus())) {
                            pendingList.add(item);
                        }
                    } catch (Exception e) {
                        Log.e("FirebaseError", "Không thể phân tích hóa đơn Pending: " + billSnapshot.getKey(), e);
                    }
                }
                listData.setValue(pendingList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "loadPendingBills bị hủy", error.toException());
            }
        });
        return listData;
    }

    public LiveData<ArrayList<BillModel>> loadAllBills() {
        MutableLiveData<ArrayList<BillModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Bills");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<BillModel> completedList = new ArrayList<>();
                if (snapshot.exists()) {
                    // SỬA LỖI: Bỏ vòng lặp lồng nhau, xử lý trực tiếp các bill
                    for (DataSnapshot billSnapshot : snapshot.getChildren()) {
                        try {
                            BillModel item = billSnapshot.getValue(BillModel.class);
                            // Kiểm tra trạng thái "Completed" như bạn đã xác nhận
                            if (item != null && "Completed".equalsIgnoreCase(item.getStatus())) {
                                completedList.add(item);
                            }
                        } catch (Exception e) {
                            Log.e("FirebaseError", "Lỗi khi phân tích hóa đơn: " + billSnapshot.getKey(), e);
                        }
                    }
                }
                listData.setValue(completedList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "loadAllBills bị hủy", error.toException());
            }
        });
        return listData;
    }

    // --- END LOGIC CODE TRANG ---

    // --- LOGIC CODE NHUNG ---
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

    // THÊM PHƯƠNG THỨC MỚI (linh hoạt hơn)

    public void updateOrderStatus(String billId, String newStatus) {
        // Dựa trên cấu trúc file JSON của bạn, node là "Bills" không phải "Bill"
        if (billId != null && !billId.isEmpty() && newStatus != null) {
            //firebaseDatabase.getReference("Bills").child(billId).child("status").setValue(newStatus);

            DatabaseReference billRef = firebaseDatabase.getReference("Bills").child(billId);
            // Tạo thông báo trong app sau khi Update bill thành công
            // Bước 1: Lấy dữ liệu của đơn hàng để có được userId
            billRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Chuyển đổi dữ liệu nhận được thành đối tượng BillModel
                    BillModel bill = snapshot.getValue(BillModel.class);

                    // Kiểm tra xem bill và userId có hợp lệ không
                    if (bill != null && bill.getUserId() != null) {
                        // Lấy userId từ đối tượng bill
                        String userId = bill.getUserId();

                        // Bước 2: Tiến hành cập nhật trạng thái
                        billRef.child("status").setValue(newStatus).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Bước 3: Tạo thông báo sau khi cập nhật thành công
                                String shortBillId = billId.substring(Math.max(0, billId.length() - 6));
                                String notificationContent = "Đơn hàng #" + shortBillId + " đã được cập nhật trạng thái thành '" + newStatus + "'.";

                                // Gọi hàm tạo thông báo với userId vừa lấy được
                                createInAppNotification(userId, notificationContent);
                            }
                        });
                    } else {
                        Log.e("UpdateStatus", "Không thể tìm thấy đơn hàng hoặc userId cho billId: " + billId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("UpdateStatus", "Lỗi khi lấy dữ liệu đơn hàng: " + error.getMessage());
                }
            });
        }
    }

    // Sửa lại hàm cancelOrder cũ để gọi hàm mới cho nhất quán
    public void cancelOrder(BillModel billToCancel) {
        if (billToCancel == null || billToCancel.getBillId() == null) {
            return;
        }
        updateOrderStatus(billToCancel.getBillId(), "Cancelled");
        String billId = billToCancel.getBillId();
        // Tạo thông báo trong app sau khi hủy bill thành công
        String notificationContent = "Đơn hàng #" + billId.substring(Math.max(0, billId.length() - 6)) + " của bạn đã được hủy";
        createInAppNotification(billToCancel.getUserId(), notificationContent);
    }
    // --- END LOGIC CODE NHUNG ---

}