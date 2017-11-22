package com.codeprinciples.recyclerviewbindingadapter.service;

import android.os.Handler;

import com.codeprinciples.recyclerviewbindingadapter.common.FailureCallback;
import com.codeprinciples.recyclerviewbindingadapter.common.SuccessCallback;
import com.codeprinciples.recyclerviewbindingadapter.models.ItemModel;
import com.codeprinciples.recyclerviewbindingadapter.models.SubitemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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

public class FakeApiController {
    private static final FakeApiController ourInstance = new FakeApiController();

    public static FakeApiController getInstance() {
        return ourInstance;
    }

    private FakeApiController() {
    }

    public void loadItems(final int page, final int pageSize, final SuccessCallback<List<ItemModel>> successCallback, final FailureCallback failureCallback){
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                int failureChance =r.nextInt(10);
                if(failureChance>=8){
                    failureCallback.onFailure("Network call failed",1);
                }else {
                    successCallback.onSuccess(getFakeItemList(page,pageSize));
                }
            }
        },500);//fake network call
    }

    private List<ItemModel> getFakeItemList(int page, int size) {
        List<ItemModel> items = new ArrayList<>();
        for(int i=0; i<size; i++){
            items.add(new ItemModel("Item#"+page*size+i, "Description Text", getFakeSubitemList()));
        }
        return items;
    }

    private List<SubitemModel> getFakeSubitemList() {
        List<SubitemModel> subitems = new ArrayList<>();
        for(int i=0; i<10; i++){
            subitems.add(new SubitemModel("Subitem#"+i));
        }
        return subitems;
    }
}
