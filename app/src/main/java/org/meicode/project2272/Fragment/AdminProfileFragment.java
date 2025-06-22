package org.meicode.project2272.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.meicode.project2272.databinding.FragmentAdminProfileBinding;

public class AdminProfileFragment extends Fragment {

    private FragmentAdminProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: Thêm logic cho tài khoản admin, ví dụ nút đăng xuất.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}