package com.codeprinciples.easyrecycleradapter;

import android.databinding.ViewDataBinding;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedHashMap;

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

public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {
    private final SectionCallback sectionCallback;
    private final boolean shouldStack;
    private final LinkedHashMap<Object, View> modelViewDictionary = new LinkedHashMap<>();

    public StickyHeaderDecoration(@NonNull SectionCallback sectionCallback) {
        this.shouldStack = false; //stack mode work in progress
        this.sectionCallback = sectionCallback;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if(shouldStack) {
            int index = parent.indexOfChild(view);
            int adapterIndex = parent.getChildAdapterPosition(view);
            if (index == 0 && sectionCallback.getHeaderModelForPosition(adapterIndex) != null) {
                outRect.top = calcOffset() - view.getHeight();
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        View topChild = parent.getChildAt(0);
        int visibleChildren = parent.getChildCount();
        if (topChild == null) {
            return;
        }

        int topChildPosition = parent.getChildAdapterPosition(topChild);
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return;
        }

        Object model = sectionCallback.getHeaderModelForPosition(topChildPosition);
        if(model!=null){
            if(modelViewDictionary.get(model)==null){
                ViewDataBinding headerBinding = sectionCallback.getHeaderForPosition(topChildPosition);
                fixLayoutSize(headerBinding.getRoot(), parent);
                headerBinding.executePendingBindings();
                modelViewDictionary.put(model, headerBinding.getRoot());
                if(shouldStack)
                    parent.invalidateItemDecorations();
            }
        }

        for(int i =topChildPosition+1; i<topChildPosition+visibleChildren; i++){
            Object otherModel = sectionCallback.getHeaderModelForPosition(i);
            if(otherModel!=null && modelViewDictionary.get(otherModel)!=null){
                modelViewDictionary.remove(otherModel);
                parent.invalidateItemDecorations();
            }
        }
        drawHeaders(c);
    }

    private void drawHeaders(Canvas c) {
        c.save();
        c.translate(0,0);
        if(shouldStack){
            for(View v : modelViewDictionary.values()){
                v.draw(c);
                c.translate(0,v.getHeight());
            }
        }else{
            View lastView = null;
            for(View v : modelViewDictionary.values()){
                lastView = v;
            }
            if(lastView!=null) {
                lastView.draw(c);
                c.translate(0, lastView.getHeight());
            }
        }

        c.restore();
    }

    private int calcOffset(){
        int offset =0;
        for(View v : modelViewDictionary.values()){
            if(shouldStack)
                offset+=v.getHeight();
            else
                offset = v.getHeight();
        }
        return offset;
    }

    /**
     * Measures the header view to make sure its size is greater than 0 and will be drawn
     * https://yoda.entelect.co.za/view/9627/how-to-android-recyclerview-item-decorations
     */
    private void fixLayoutSize(View view, ViewGroup parent) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(),
                View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight(),
                View.MeasureSpec.UNSPECIFIED);

        int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                parent.getPaddingLeft() + parent.getPaddingRight(),
                view.getLayoutParams().width);
        int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                parent.getPaddingTop() + parent.getPaddingBottom(),
                view.getLayoutParams().height);

        view.measure(childWidth,
                childHeight);

        view.layout(0,
                0,
                view.getMeasuredWidth(),
                view.getMeasuredHeight());
    }

    public interface SectionCallback {

        Object getHeaderModelForPosition(int position);
        ViewDataBinding getHeaderForPosition(int position);
    }
}
