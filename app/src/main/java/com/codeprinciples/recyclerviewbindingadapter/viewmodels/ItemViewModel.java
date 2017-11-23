package com.codeprinciples.recyclerviewbindingadapter.viewmodels;

import com.codeprinciples.recyclerviewbindingadapter.common.ItemClickCallback;
import com.codeprinciples.recyclerviewbindingadapter.models.ItemModel;
import com.codeprinciples.recyclerviewbindingadapter.models.SubitemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Oleksiy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

public class ItemViewModel {
    private ItemModel model;
    private ItemViewModelEventsInterface listener;
    private List<SubitemViewModel> subitemViewModels;
    private boolean isExapanded;
    public ItemViewModel(ItemModel model) {
        this.model = model;
    }

    public ItemModel getModel() {
        return model;
    }

    public void setListener(ItemViewModelEventsInterface listener) {
        this.listener = listener;
    }

    public void onClick(){
        listener.onItemClick(this);
    }

    public void onDeleteClick(){
        listener.onItemDeleteClick(this, isExapanded);
    }

    public void onExpandClick(){
        isExapanded = !isExapanded;
        listener.onItemExpandClick(this, isExapanded);
    }

    public List<SubitemViewModel> getSubitemViewModels() {
        if (subitemViewModels == null)
            subitemViewModels = new ArrayList<>();
        for (SubitemModel subitemModel : model.getSubitems()) {
            SubitemViewModel subitemViewModel = new SubitemViewModel(subitemModel);
            subitemViewModel.setClickCallback(new ItemClickCallback<SubitemViewModel>() {
                @Override
                public void onItemClick(SubitemViewModel item) {
                    listener.onSubitemClick(item);
                }
            });
            subitemViewModels.add(subitemViewModel);
        }
        return subitemViewModels;
    }

    public interface ItemViewModelEventsInterface extends ItemClickCallback<ItemViewModel> {
        void onItemDeleteClick(ItemViewModel item, boolean isExpanded);

        void onItemExpandClick(ItemViewModel item, boolean isExpanded);

        void onSubitemClick(SubitemViewModel subitemModel);
    }
}
