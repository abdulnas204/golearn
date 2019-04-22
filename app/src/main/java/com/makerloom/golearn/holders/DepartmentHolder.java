package com.makerloom.golearn.holders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.makerloom.common.utils.Keys;
import com.makerloom.golearn.R;
import com.makerloom.golearn.models.Department;
import com.makerloom.golearn.screens.CoursesActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by michael on 4/11/18.
 */

public class DepartmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private Department department;

    public TextView departmentNameTV;
    public TextView facultyNameTV;
    public CardView cardView;
    public TextView shortNameTV;
    public MaterialCardView shortNameCV;

    public DepartmentHolder (View view, Context context) {
        super(view);

        departmentNameTV = view.findViewById(R.id.department);
        facultyNameTV = view.findViewById(R.id.faculty);
        cardView = view.findViewById(R.id.card_view);
        shortNameTV = view.findViewById(R.id.department_name_tv);
        shortNameCV = view.findViewById(R.id.department_name_cv);

        this.context = context;
    }

    public void setDepartment (Department department) {
        this.department = department;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, CoursesActivity.class);
        intent.putExtra(Keys.DEPARTMENT_NAME_KEY, department.getName());
        context.startActivity(intent);
    }
}
