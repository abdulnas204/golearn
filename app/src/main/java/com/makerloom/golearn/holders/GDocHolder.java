package com.makerloom.golearn.holders;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.makerloom.common.utils.Constants;
import com.makerloom.common.utils.Keys;
import com.makerloom.golearn.R;
import com.makerloom.golearn.models.Document;
import com.makerloom.golearn.screens.HomeActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by michael on 4/11/18.
 */

public class GDocHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private Document doc;

    public TextView docTitleTV;
    public TextView fileSizeTV, lastUpdateTV;
    public CardView cardView;
    public TextView extTV;
    public MaterialCardView extCV;
    public FancyButton getBtn;

    public GDocHolder(View view, Context context) {
        super(view);

        docTitleTV = view.findViewById(R.id.doc_title_tv);
        fileSizeTV = view.findViewById(R.id.file_size_tv);
        lastUpdateTV = view.findViewById(R.id.last_update_tv);
        cardView = view.findViewById(R.id.card_view);
        extTV = view.findViewById(R.id.ext_tv);
        extCV = view.findViewById(R.id.ext_cv);
        getBtn = view.findViewById(R.id.get_btn);
        getBtn.getTextViewObject().setTypeface(Typeface.DEFAULT_BOLD);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onClick(v);
            }
        });

        this.context = context;
    }

    public void setDocument(Document doc) {
        this.doc = doc;
    }

    private String getDeptName (AppCompatActivity context) {
        if (context.getIntent().hasExtra(Keys.DEPARTMENT_NAME_KEY)) {
            return context.getIntent().getStringExtra(Keys.DEPARTMENT_NAME_KEY);
        }
        else {
            return Constants.DEFAULT_DEPARTMENT_NAME;
        }
    }

    @Override
    public void onClick(View v) {
        _onClick(v);
    }

    public void _onClick (View v) {
        ((HomeActivity) context).openDocument(doc.getFilePath());
    }
}
