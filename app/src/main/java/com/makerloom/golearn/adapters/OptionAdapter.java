package com.makerloom.golearn.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makerloom.golearn.R;
import com.makerloom.golearn.holders.OptionHolder;
import com.makerloom.golearn.models.Question;
import com.makerloom.golearn.screens.CorrectionActivity;
import com.makerloom.golearn.screens.TestActivity;
import com.makerloom.golearn.utils.TextViewUtils;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by michael on 4/11/18.
 */

public class OptionAdapter  extends RecyclerView.Adapter<OptionHolder> {
    private Question question;
    private Context context;

    public OptionAdapter (Context context, Question question) {
        this.context = context;
        this.question = question;

        int states [][] = { { android.R.attr.state_checked }, { } };
        int uncheckedColor = ContextCompat.getColor(context, R.color.white);
        int [] correctColors = { ContextCompat.getColor(context, R.color.green), uncheckedColor };
        int [] wrongColors = { ContextCompat.getColor(context, R.color.red), uncheckedColor };

        correct = new ColorStateList(states, correctColors);
        wrong = new ColorStateList(states, wrongColors);
    }

    @Override
    public OptionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (context instanceof CorrectionActivity) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.correction_option_card, null);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.option_card, null);
        }
        return new OptionHolder(view, context);
    }

    private ColorStateList correct, wrong;

    @Override
    public void onBindViewHolder(OptionHolder holder, int position) {
        String option = question.getOptions().get(position);
        boolean lastOpt = position == getItemCount() - 1;

        holder.setOption(option);

        holder.optionTV.setText(TextViewUtils.Companion.fromHtml(option));
        holder.optionCB.setChecked(false);
        holder.optionCB.setClickable(false);
        holder.cardView.setOnClickListener(getOnClickOption(question, position));

        question.addOptionCheckBoxes(holder.optionCB, position);
        if (holder.isCorrection()) {
            question.addCorrectOptionCheckBox(holder.optionCBCorrect, position);
            question.addWrongOptionCheckBox(holder.optionCBWrong, position);
        }

        if (lastOpt) {
            if (context instanceof CorrectionActivity) {
                question.doCorrection();
            }
            if (context instanceof TestActivity) {
                question.tickAnsweredCheckBox();
            }
        }
    }

    private View.OnClickListener getOnClickOption (final Question question, final int index) {
        View.OnClickListener onClickOption = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof TestActivity) {
                    question.selectOption(question.getOptions().get(index), index);
                    // TODO:
                    // Make this work properly
//                    if (question.isAnswered()) {
//                        question.boldenNumberBtn(context);
//                    }
//                    else {
//                        question.unBoldenNumberBtn(context);
//                    }
                }
            }
        };

        return onClickOption;
    }

    @Override
    public int getItemCount() {
        return question.getOptions().size();
    }
}
