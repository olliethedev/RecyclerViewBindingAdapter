package com.codeprinciples.easyrecycleradapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
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

public class EasyRecyclerAdapter extends RecyclerView.Adapter<EasyRecyclerAdapter.BindingViewHolder> {
    private ObservableList<Object> data;
    private HashMap<Integer,List<Pair<Integer,Class>>> dictionary;

    public EasyRecyclerAdapter(ObservableList<Object> data) {
        this.data = data;
        data.addOnListChangedCallback(new ObservableListCallback());
        dictionary = new HashMap<>();
    }

    public EasyRecyclerAdapter addMapping(@LayoutRes int layoutId, int bindingResId, Class modelClass){
        if(dictionary.containsKey(layoutId)){
            throw new IllegalArgumentException("Adapter only supports one variable per layout!");
//            List<Pair<Integer,Class>>bindings=dictionary.get(layoutId);
//            for(Pair<Integer,Class> p : bindings){
//                if(p.first.equals(bindingResId)){
//                    throw new IllegalArgumentException("Already added a mapping with this binding variable id!");
//                }else if(p.second.equals(modelClass)){
//                    throw new IllegalArgumentException("Already added a mapping for this model type!")
//                }
//            }
//            bindings.add(new Pair<>(bindingResId,modelClass));
        }else {
            List<Pair<Integer,Class>> l = new ArrayList<>();
            l.add(new Pair<>(bindingResId,modelClass));
            dictionary.put(layoutId,l);
        }
        return this;
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),viewType,parent,false));
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        AdapterDataItem dataItem = map(data.get(position));
        for(Pair<Integer, Object> idObjectPair: dataItem.idModelPairs){
            holder.bind(idObjectPair.first, idObjectPair.second);
        }
        holder.binding.executePendingBindings();
    }

    private AdapterDataItem map(Object o) {
        for(Integer key : dictionary.keySet()){
            List<Pair<Integer,Class>> value = dictionary.get(key);
            for(Pair<Integer,Class> pair : value){
                if(o.getClass().equals(pair.second)){
                    return new AdapterDataItem(key,pair.first,o);
                }
            }
        }
        throw new IllegalStateException("Unknown object type. Please map it to a layout first");
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return map(data.get(position)).layoutId;
    }


    private class ObservableListCallback extends  ObservableList.OnListChangedCallback<ObservableList<Object>>{

        @Override
        public void onChanged(ObservableList<Object> sender) {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<Object> sender, int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeInserted(ObservableList<Object> sender, int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(ObservableList<Object> sender, int fromPosition, int toPosition, int itemCount) {
            notifyDataSetChanged(); // not sure how to notify adapter of this event
        }

        @Override
        public void onItemRangeRemoved(ObservableList<Object> sender, int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }


    public class BindingViewHolder extends RecyclerView.ViewHolder{
        private ViewDataBinding binding;

        public BindingViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

        public void bind(int varId, Object obj){
            this.binding.setVariable(varId, obj);
        }
    }

    public static class AdapterDataItem {
        public int layoutId;
        public List<Pair<Integer,Object>> idModelPairs;

        public AdapterDataItem(int layoutId, int variableId, Object model) {
            this.layoutId = layoutId;
            this.idModelPairs = new ArrayList<>();
            this.idModelPairs.add(new Pair<>(variableId,model));
        }



        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AdapterDataItem that = (AdapterDataItem) o;

            if (layoutId != that.layoutId) return false;
            return idModelPairs != null ? idModelPairs.equals(that.idModelPairs) : that.idModelPairs == null;

        }

        @Override
        public int hashCode() {
            int result = layoutId;
            result = 31 * result + (idModelPairs != null ? idModelPairs.hashCode() : 0);
            return result;
        }
    }
}
