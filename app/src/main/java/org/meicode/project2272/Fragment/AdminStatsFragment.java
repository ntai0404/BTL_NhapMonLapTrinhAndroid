package org.meicode.project2272.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.FragmentAdminStatsBinding;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminStatsFragment extends Fragment {

    private FragmentAdminStatsBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        fetchAndProcessRevenueData();
    }

    private void fetchAndProcessRevenueData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.revenueTextView.setVisibility(View.GONE);

        viewModel.loadAllBills().observe(getViewLifecycleOwner(), bills -> {
            if (bills != null && !bills.isEmpty()) {
                Map<String, Double> weeklyRevenue = calculateWeeklyRevenue(bills);
                displayRevenue(weeklyRevenue);
            } else {
                binding.revenueTextView.setText("Không có dữ liệu hóa đơn.");
            }
            binding.progressBar.setVisibility(View.GONE);
            binding.revenueTextView.setVisibility(View.VISIBLE);
        });
    }

    private Map<String, Double> calculateWeeklyRevenue(ArrayList<BillModel> bills) {
        Map<String, Double> weeklyRevenue = new HashMap<>();

        // SỬA LỖI: Đổi định dạng ngày tháng để khớp với dữ liệu Firebase của bạn
        // Định dạng cũ: "yyyy-MM-dd'T'HH:mm:ss'Z'"
        // Định dạng mới khớp với "19-06-2025 18:26:57"
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (BillModel bill : bills) {
            if (bill.getCreatedAt() == null) continue;
            try {
                Date date = sdf.parse(bill.getCreatedAt());
                if (date != null) {
                    calendar.setTime(date);
                    int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
                    int year = calendar.get(Calendar.YEAR);
                    String weekKey = "Tuần " + weekOfYear + ", " + year;

                    double currentRevenue = weeklyRevenue.getOrDefault(weekKey, 0.0);
                    weeklyRevenue.put(weekKey, currentRevenue + bill.getTotalAmount());
                }
            } catch (ParseException e) {
                // Lỗi này sẽ được in ra trong Logcat nếu định dạng ngày tháng vẫn sai
                e.printStackTrace();
            }
        }

        // Sắp xếp map theo key (chuỗi tuần) để hiển thị theo thứ tự
        List<Map.Entry<String, Double>> list = new LinkedList<>(weeklyRevenue.entrySet());
        Collections.sort(list, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));

        Map<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private void displayRevenue(Map<String, Double> weeklyRevenue) {
        StringBuilder displayText = new StringBuilder();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (Map.Entry<String, Double> entry : weeklyRevenue.entrySet()) {
            String formattedAmount = currencyFormatter.format(entry.getValue());
            displayText.append(entry.getKey()).append(": ").append(formattedAmount).append("\n\n");
        }
        binding.revenueTextView.setText(displayText.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}