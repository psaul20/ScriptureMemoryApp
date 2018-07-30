package com.scripturememory.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scripturememory.R;
import com.scripturememory.models.AddVerseExpandableGroup;
import com.scripturememory.models.AddVerseExpandableItem;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

public class AddPsgAdapter extends ExpandableRecyclerViewAdapter<AddPsgAdapter.ParentViewHolder, AddPsgAdapter.SubViewHolder> {

    private Context context;
    private OnChildClickListener listener;

    public AddPsgAdapter(List<? extends ExpandableGroup> groups, Context context) {
        super(groups);
        this.context = context;
        this.listener = ((OnChildClickListener) context);
    }

    @Override
    public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.parent_row_add_verse, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public SubViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.child_row_add_verse, parent, false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(SubViewHolder holder, final int flatPosition, final ExpandableGroup group, final int childIndex) {
        final AddVerseExpandableItem item = ((AddVerseExpandableGroup) group).getItems().get(childIndex);
        holder.content.setText(item.getTitle());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddVerseExpandableGroup) group).setSelection(item.getTitle());
                toggleGroup(group);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onItemClick((AddVerseExpandableGroup) group);
                }
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.title.setText(group.getTitle());
        String psgSelection = ((AddVerseExpandableGroup) group).getSelection();
        holder.selection.setText(psgSelection);
    }

    public class ParentViewHolder extends GroupViewHolder {

        private TextView title;
        private TextView selection;
        private ImageView dropdownIcon;
        private View mView;

        public ParentViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.dropdownTitle);
            selection = itemView.findViewById(R.id.dropdownPreview);
            dropdownIcon = itemView.findViewById(R.id.dropdownIcon);
            mView = itemView;
        }
    }

    public class SubViewHolder extends ChildViewHolder {

        private TextView content;
        private View mView;

        public SubViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.dropdownContent);
            mView = itemView;
        }

    }

    public interface OnChildClickListener {
        void onItemClick(AddVerseExpandableGroup group);
    }

}
