package com.makerloom.golearn.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makerloom.golearn.R;
import com.makerloom.golearn.holders.QuestionNumberHolder;
import com.makerloom.golearn.screens.TestActivity;

import java.util.List;

/**
 * Created by michael on 4/11/18.
 */

public class QuestionNumberAdapter extends RecyclerView.Adapter<QuestionNumberHolder> {
    private List<Integer> numbers;
    private Context context;

    public QuestionNumberAdapter (Context context, List<Integer> numbers) {
        this.context = context;
        this.numbers = numbers;
    }

    @Override
    public QuestionNumberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_number, null);
        return new QuestionNumberHolder(view, context);
    }

    @Override
    public void onBindViewHolder(QuestionNumberHolder holder, int position) {
        Integer number = numbers.get(position);
        boolean last = position == getItemCount() - 1;

        holder.setQuestionNumber(number);

        holder.numberFB.setText(String.valueOf(number));

        if (context instanceof TestActivity) {
            TestActivity testActivity = (TestActivity) context;
            testActivity.numberBtns.add(position, holder.numberFB);

            if (last) {
                testActivity.goToQuestion();
                testActivity.handleNumberBtns();
            }
        }
    }

    @Override
    public int getItemCount() {
        return numbers.size();
    }
}