package com.devtau.rx;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.StringViewHolder> {

	private final List<String> userList;


	public ListAdapter(List<String> userList) {
		this.userList = userList;
	}

	@Override
	public StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
		ButterKnife.bind(this, view);
		return new StringViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final StringViewHolder holder, int position) {
		holder.path = userList.get(position);
		holder.textView.setText(holder.path);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(view.getContext(), holder.path, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public int getItemCount() {
		return userList.size();
	}



	static class StringViewHolder extends RecyclerView.ViewHolder {

		View itemView;
		String path;

		@Bind(R.id.text_view)
		TextView textView;

		StringViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			this.itemView = itemView;
		}
	}
}
