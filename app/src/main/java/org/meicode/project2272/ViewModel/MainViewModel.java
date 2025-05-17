package org.meicode.project2272.ViewModel;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import org.meicode.project2272.Domain.BannerModel;
import org.meicode.project2272.Domain.CategoryModel;
import org.meicode.project2272.Respository.MainRespository;
import java.util.ArrayList;


public class MainViewModel extends ViewModel {
    private final MainRespository respository= new MainRespository();
    public LiveData<ArrayList<CategoryModel>> loadCategory(){
        return respository.loadCategory();
    };
    public LiveData<ArrayList<BannerModel>> loadBanner(){
        return respository.loadBanner();
    }
}
