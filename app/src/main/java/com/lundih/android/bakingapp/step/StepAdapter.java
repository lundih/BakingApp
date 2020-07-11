package com.lundih.android.bakingapp.step;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.lundih.android.bakingapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private RecyclerView recyclerView;
    private final List<Step> steps;
    private final OnStepItemClickedListener listener;
    private final boolean isTabMode;
    private final int previousPosition;

    public StepAdapter(Context context, List<Step> steps, OnStepItemClickedListener listener, boolean isTabMode,int previousPosition) {
        this.context =context;
        this.steps = steps;
        this.listener = listener;
        this.isTabMode = isTabMode;
        this.previousPosition = previousPosition;
    }

    public interface OnStepItemClickedListener {
        void onStepItemClicked(int position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        recyclerView = (RecyclerView) parent;

        return new StepAdapter.StepItem(LayoutInflater.from(context).inflate(R.layout.item_step, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (steps != null) {
            if (steps.get(position).getShortDescription() != null) {
                String text = "";
                if (steps.get(position).getStepID() != 0) {
                    text = context.getString(R.string.text_view_step_item_index_label) + steps.get(position).getStepID() + context.getString(R.string.colon) + context.getString(R.string.white_space);
                }
                text = text + steps.get(position).getShortDescription();
                ((StepItem)holder).textViewStepShortDescription.setText(text);
            }
            if (steps.get(position).getThumbnailURL() != null) {
                if (!steps.get(position).getThumbnailURL().equals("")) {
                    Picasso.get().load(steps.get(position).getThumbnailURL())
                            .placeholder(R.drawable.image_step_thumbnail_placeholder)
                            .error(R.drawable.image_step_thumbnail_placeholder2)
                            .into(((StepItem)holder).imageViewStepItem);
                } else ((StepItem)holder).imageViewStepItem.setImageResource(R.drawable.image_step_thumbnail_placeholder);
            }

            if (isTabMode && position == previousPosition) {
                resizeCardMargins(((StepItem) holder).cardViewStepItem, true);
            }
            ((StepItem)holder).cardViewStepItem.setOnClickListener(view -> {
                // Perform callback to StepFragment for implementation
                if (listener != null) listener.onStepItemClicked(position);
                // If in tab mode, highlight the currently selected card
                if (isTabMode) {
                    // Resize all cards' margins
                    for (int i = 0; i < steps.size(); i++){
                        View viewItem = recyclerView.getChildAt(i);
                        CardView cardView = viewItem.findViewById(R.id.cardViewStepItem);
                        resizeCardMargins(cardView, false);
                    }
                    // Resize currently selected card
                    resizeCardMargins(((StepItem) holder).cardViewStepItem, true);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (steps != null) return steps.size();
        else return 0;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    class StepItem extends RecyclerView.ViewHolder {
        final CardView cardViewStepItem;
        final TextView textViewStepShortDescription;
        final ImageView imageViewStepItem;

        public StepItem(@NonNull View stepItem) {
            super(stepItem);

            cardViewStepItem = stepItem.findViewById(R.id.cardViewStepItem);
            textViewStepShortDescription = stepItem.findViewById(R.id.textViewStepShortDescription);
            imageViewStepItem = stepItem.findViewById(R.id.imageViewStepItemImage);
        }
    }

    void resizeCardMargins(CardView cardView, boolean isToShrink) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) cardView.getLayoutParams();
        if (isToShrink)
            // Parameters for left, top, right, bottom margins
            params.setMargins(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6));
        else
            params.setMargins(dpToPx(0), dpToPx(4), dpToPx(0), dpToPx(4));
        cardView.setLayoutParams(params);
    }

    int dpToPx(int dpDimension){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpDimension, Resources.getSystem().getDisplayMetrics());
    }
}
