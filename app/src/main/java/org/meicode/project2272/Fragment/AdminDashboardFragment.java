package org.meicode.project2272.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.meicode.project2272.Model.BillModel;
import org.meicode.project2272.ViewModel.MainViewModel;
import org.meicode.project2272.databinding.FragmentAdminDashboardBinding;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdminDashboardFragment extends Fragment {

    private FragmentAdminDashboardBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        loadDashboardData();
    }

    private void loadDashboardData() {
        // Luồng 1: Lấy các hóa đơn "Completed" để tính doanh thu và vẽ biểu đồ
        viewModel.loadAllBills().observe(getViewLifecycleOwner(), completedBills -> {
            if (completedBills != null) {
                updateRevenueUI(completedBills);
                setupBarChart(completedBills);
            }
        });

        // Luồng 2: Lấy các hóa đơn "Pending" để đếm và hiển thị
        viewModel.loadPendingBills().observe(getViewLifecycleOwner(), pendingBills -> {
            if (pendingBills != null) {
                // Chỉ cần lấy kích thước của danh sách là có số đơn hàng chờ
                binding.pendingOrdersTxt.setText(String.valueOf(pendingBills.size()));
            }
        });
    }

    // Đổi tên phương thức để rõ ràng hơn
    private void updateRevenueUI(ArrayList<BillModel> completedBills) {
        double currentWeekRevenue = 0;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Danh sách này chỉ chứa các hóa đơn "Completed", nên ta chỉ cần cộng dồn
        for (BillModel bill : completedBills) {
            currentWeekRevenue += bill.getTotalAmount();
        }
        binding.revenueTxt.setText(currencyFormatter.format(currentWeekRevenue));
    }

    private void setupBarChart(ArrayList<BillModel> bills) {
        // Khởi tạo mảng doanh thu cho 7 ngày trong tuần
        float[] dailyRevenue = new float[7];
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Calendar billCalendar = Calendar.getInstance();
        Calendar todayCalendar = Calendar.getInstance();
        int currentWeek = todayCalendar.get(Calendar.WEEK_OF_YEAR);
        int currentYear = todayCalendar.get(Calendar.YEAR);

        for (BillModel bill : bills) {
            if (bill.getCreatedAt() == null) continue;
            try {
                Date date = sdf.parse(bill.getCreatedAt());
                billCalendar.setTime(date);
                int billWeek = billCalendar.get(Calendar.WEEK_OF_YEAR);
                int billYear = billCalendar.get(Calendar.YEAR);

                // Chỉ tính các hóa đơn trong tuần này
                if (billWeek == currentWeek && billYear == currentYear) {
                    int dayOfWeek = billCalendar.get(Calendar.DAY_OF_WEEK); // Chủ nhật = 1, T2 = 2, ...
                    // Chuyển về index của mảng (0=T2, 1=T3, ..., 6=CN)
                    int arrayIndex = (dayOfWeek == Calendar.SUNDAY) ? 6 : dayOfWeek - 2;
                    if (arrayIndex >= 0 && arrayIndex < 7) {
                        dailyRevenue[arrayIndex] += bill.getTotalAmount();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < dailyRevenue.length; i++) {
            entries.add(new BarEntry(i, dailyRevenue[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        binding.revenueChart.setData(barData);

        // Tùy chỉnh biểu đồ
        binding.revenueChart.getDescription().setEnabled(false);
        binding.revenueChart.getLegend().setEnabled(false);
        binding.revenueChart.setFitBars(true);

        // Tùy chỉnh trục X (trục ngang)
        XAxis xAxis = binding.revenueChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] days = new String[]{"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setGranularity(1f);

        // Vẽ lại biểu đồ
        binding.revenueChart.invalidate();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}