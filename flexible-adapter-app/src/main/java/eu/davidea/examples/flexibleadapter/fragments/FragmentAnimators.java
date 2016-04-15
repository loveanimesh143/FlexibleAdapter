package eu.davidea.examples.flexibleadapter.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import eu.davidea.examples.flexibleadapter.ExampleAdapter;
import eu.davidea.examples.flexibleadapter.MainActivity;
import eu.davidea.examples.flexibleadapter.R;
import eu.davidea.examples.flexibleadapter.services.DatabaseService;
import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.common.DividerItemDecoration;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flipview.FlipView;
import eu.davidea.utils.Utils;

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FragmentAnimators  extends AbstractFragment {

	public static final String TAG = FragmentAnimators.class.getSimpleName();

	private ExampleAdapter mAdapter;

	public static FragmentAnimators newInstance(int columnCount) {
		FragmentAnimators fragment = new FragmentAnimators();
		Bundle args = new Bundle();
		args.putInt(ARG_COLUMN_COUNT, columnCount);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FragmentAnimators() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//Settings for FlipView
		FlipView.resetLayoutAnimationDelay(true, 1000L);

		//Create New Database and Initialize RecyclerView
		DatabaseService.getInstance().createAnimatorsDatabase();
		initializeRecyclerView(savedInstanceState);

		//Settings for FlipView
		FlipView.stopLayoutAnimation();
	}

	@SuppressWarnings({"ConstantConditions", "NullableProblems"})
	private void initializeRecyclerView(Bundle savedInstanceState) {
		mAdapter = new ExampleAdapter(getActivity());
		//Experimenting NEW features (v5.0.0)
		mAdapter.setAnimationOnScrolling(true);
		mAdapter.setAnimationOnReverseScrolling(true);
		mAdapter.setAutoCollapseOnExpand(false);
		mAdapter.setAutoScrollOnExpand(true);
		mAdapter.setRemoveOrphanHeaders(false);
		mAdapter.setUnlinkAllItemsOnRemoveHeaders(false);
		mRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
		mRecyclerView.setLayoutManager(new SmoothScrollLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
		mRecyclerView.setAdapter(mAdapter);
		mRecyclerView.setHasFixedSize(true); //Size of RV will not change
		mRecyclerView.setItemAnimator(new DefaultItemAnimator() {
			@Override
			public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
				//NOTE: This allows to receive Payload objects when notifyItemChanged is called by the Adapter!!!
				return true;
			}
		});
		//mRecyclerView.setItemAnimator(new SlideInRightAnimator());
		mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider));

		//Add FastScroll to the RecyclerView, after the Adapter has been attached the RecyclerView!!!
		mAdapter.setFastScroller((FastScroller) getActivity().findViewById(R.id.fast_scroller),
				Utils.getColorAccent(getActivity()), (MainActivity) getActivity());
		//Experimenting NEW features (v5.0.0)
		mAdapter.setLongPressDragEnabled(true);//Enable long press to drag items
		mAdapter.setSwipeEnabled(true);//Enable swipe items
		mAdapter.setDisplayHeadersAtStartUp(true);//Show Headers at startUp!
		//Add sample item on the top (not belongs to the library)
		mAdapter.addUserLearnedSelection(savedInstanceState == null);

		SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
		mListener.onAdapterChange(swipeRefreshLayout, mRecyclerView);
	}

}