package com.codeprinciples.recyclerviewbindingadapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.codeprinciples.easyrecycleradapter.EasyRecyclerAdapter;
import com.codeprinciples.easyrecycleradapter.StickyHeaderDecoration;
import com.codeprinciples.recyclerviewbindingadapter.common.FailureCallback;
import com.codeprinciples.recyclerviewbindingadapter.common.ItemClickCallback;
import com.codeprinciples.recyclerviewbindingadapter.common.SuccessCallback;
import com.codeprinciples.recyclerviewbindingadapter.databinding.LayoutHeadingRowBinding;
import com.codeprinciples.recyclerviewbindingadapter.models.HeadingModel;
import com.codeprinciples.recyclerviewbindingadapter.models.LoadMoreModel;
import com.codeprinciples.recyclerviewbindingadapter.service.FakeDataRepository;
import com.codeprinciples.recyclerviewbindingadapter.viewmodels.HeadingViewModel;
import com.codeprinciples.recyclerviewbindingadapter.viewmodels.ItemViewModel;
import com.codeprinciples.recyclerviewbindingadapter.viewmodels.LoadMoreViewModel;
import com.codeprinciples.recyclerviewbindingadapter.viewmodels.SubitemViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemViewModel.ItemViewModelEventsInterface {
    private RecyclerView recyclerView;
    private ObservableArrayList<Object> dataList;
    private LoadMoreViewModel loadMoreViewModel;
    private int headingCount =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        dataList = initialize();
        EasyRecyclerAdapter adapter = new EasyRecyclerAdapter(dataList);//step 4: initialize adapter with observable collection that will hold all the view models

        adapter.addMapping(R.layout.layout_heading_row,BR.headingViewModel,HeadingViewModel.class)//step 5: add mappings between layouts and view model types
            .addMapping(R.layout.layout_load_more_row, BR.loadMoreViewModel, LoadMoreViewModel.class)
            .addMapping(R.layout.layout_item_row, BR.itemViewModel, ItemViewModel.class)
            .addMapping(R.layout.layout_subitem_row, BR.subitemViewModel, SubitemViewModel.class);

        recyclerView.setAdapter(adapter);//step 6: set adapter

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new StickyHeaderDecoration( new StickyHeaderDecoration.SectionCallback() {
            @Override
            public Object getHeaderModelForPosition(int position) {
                if(dataList.get(position) instanceof HeadingViewModel)
                    return dataList.get(position);
                return null;
            }

            @Override
            public ViewDataBinding getHeaderForPosition(int position) {
                if(dataList.get(position) instanceof HeadingViewModel){
                    LayoutHeadingRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(MainActivity.this),R.layout.layout_heading_row, recyclerView,false);
                    binding.getRoot().setBackgroundColor(Color.parseColor("#00ff00"));
                    binding.setHeadingViewModel((HeadingViewModel)dataList.get(position));
                    return binding;
                }
                return null;
            }
        }));

        loadMore();
    }

    private ObservableArrayList<Object> initialize() {
        ObservableArrayList<Object> list= new ObservableArrayList<>();
        loadMoreViewModel = new LoadMoreViewModel(new LoadMoreModel("Load More"), new ItemClickCallback<LoadMoreViewModel>() {
            @Override
            public void onItemClick(LoadMoreViewModel item) {
                loadMore();
            }
        });
        list.add(loadMoreViewModel);
        return list;
    }

    private void loadMore() {
        loadMoreViewModel.getLoadMoreModel().isLoading.set(true);
        FakeDataRepository.getInstance().getNextItems(new SuccessCallback<List<ItemViewModel>>() {
            @Override
            public void onSuccess(List<ItemViewModel> data) {
                dataList.add(dataList.size()-1, new HeadingViewModel(new HeadingModel("Items Heading #"+headingCount++)));
                for (ItemViewModel item : data) {
                    item.setListener(MainActivity.this);
                }
                dataList.addAll(dataList.size()-1, data); //insert above "Load More" cell
                loadMoreViewModel.getLoadMoreModel().isLoading.set(false);
            }
        }, new FailureCallback() {
            @Override
            public void onFailure(String reason, int code) {
                loadMoreViewModel.getLoadMoreModel().isLoading.set(false);
                Toast.makeText(MainActivity.this, "Failed to load dataList, try again.", Toast.LENGTH_SHORT).show();
                loadMoreViewModel.getLoadMoreModel().isLoading.set(false);
            }
        });
    }

    @Override
    public void onItemClick(ItemViewModel item) {
        Toast.makeText(this, item.getModel().getItemTitle() + " clicked.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemDeleteClick(ItemViewModel item, boolean isExapanded) {
        List<Object> toRemove = new ArrayList<>();
        toRemove.add(item);
        if (isExapanded)
            toRemove.addAll(item.getSubitemViewModels());
        for(Object o : toRemove)
            dataList.remove(o);//removeAll() does not work with ObservableArrayList
    }

    @Override
    public void onItemExpandClick(ItemViewModel item, boolean isExapanded) {
        if (isExapanded) {
            dataList.addAll(dataList.indexOf(item)+1, item.getSubitemViewModels());
        } else {
            for(SubitemViewModel subitemViewModel : item.getSubitemViewModels())
                dataList.remove(subitemViewModel); //removeAll() does not work with ObservableArrayList
        }
    }

    @Override
    public void onSubitemClick(SubitemViewModel subitemModel) {
        Toast.makeText(this, subitemModel.getSubitemModel().getSubitemTitle() + " clicked.", Toast.LENGTH_SHORT).show();
    }
}
