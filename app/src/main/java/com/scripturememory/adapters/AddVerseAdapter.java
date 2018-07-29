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

public class AddVerseAdapter extends ExpandableRecyclerViewAdapter<AddVerseAdapter.ParentViewHolder, AddVerseAdapter.SubViewHolder> {

    private Context context;
    private OnChildClickListener listener;

    public AddVerseAdapter(List<? extends ExpandableGroup> groups, Context context) {
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
        holder.setContent(item.getTitle());
        holder.getContent().setOnClickListener(new View.OnClickListener() {
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
        holder.setTitle(group);
        String passageSelection = ((AddVerseExpandableGroup) group).getSelection();
        holder.setSelection(passageSelection);
    }

    public class ParentViewHolder extends GroupViewHolder {

        private TextView title;
        private TextView selection;
        private ImageView dropdownIcon;
        private View component; // allows for clicking of the whole component

        public ParentViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.dropdownTitle);
            selection = itemView.findViewById(R.id.dropdownPreview);
            dropdownIcon = itemView.findViewById(R.id.dropdownIcon);
            component = itemView;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(ExpandableGroup group) {
            this.title.setText(group.getTitle());
        }

        public TextView getSelection() {
            return selection;
        }

        public void setSelection(String selection) {
            this.selection.setText(selection);
        }

        public ImageView getDropdownIcon() {
            return dropdownIcon;
        }

        public View getComponent() {
            return component;
        }
    }

    public class SubViewHolder extends ChildViewHolder {

        private TextView content;

        public SubViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.dropdownContent);
        }

        public TextView getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content.setText(content);
        }

    }

    public interface OnChildClickListener {
        void onItemClick(AddVerseExpandableGroup group);
    }

}
