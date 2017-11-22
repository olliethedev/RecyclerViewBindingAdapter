package com.codeprinciples.recyclerviewbindingadapter.service;

import com.codeprinciples.recyclerviewbindingadapter.common.FailureCallback;
import com.codeprinciples.recyclerviewbindingadapter.common.SuccessCallback;
import com.codeprinciples.recyclerviewbindingadapter.models.ItemModel;
import com.codeprinciples.recyclerviewbindingadapter.viewmodels.ItemViewModel;

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

public class FakeDataRepository {
    private static final int PAGE_SIZE=5;
    private int currentPage;
    private static final FakeDataRepository ourInstance = new FakeDataRepository();

    public static FakeDataRepository getInstance() {
        return ourInstance;
    }

    private FakeDataRepository() {
    }

    public void getNextItems(final SuccessCallback<List<ItemViewModel>> successCallback, FailureCallback failureCallback){
        FakeApiController.getInstance().loadItems(currentPage, PAGE_SIZE, new SuccessCallback<List<ItemModel>>() {
            @Override
            public void onSuccess(List<ItemModel> data) {
                successCallback.onSuccess(map(data));
                ++currentPage;
            }
        }, failureCallback);
    }

    private List<ItemViewModel> map(List<ItemModel> data) {
        List<ItemViewModel> viewModels = new ArrayList<>();
        for(ItemModel item : data){
            viewModels.add(new ItemViewModel(item));
        }
        return viewModels;
    }

}
