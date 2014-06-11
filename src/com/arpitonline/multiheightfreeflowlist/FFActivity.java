package com.arpitonline.multiheightfreeflowlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.core.Section;
import com.comcast.freeflow.core.SectionedAdapter;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.layouts.FreeFlowLayoutBase;
import com.comcast.freeflow.layouts.VLayout;
import com.comcast.freeflow.utils.ViewUtils;

public class FFActivity extends Activity {

	private AdaptiveTextHeightLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ff);

		FreeFlowContainer container = (FreeFlowContainer) findViewById(R.id.container);

		String[] strings = new String[] {
				"1) A layout engine for Android that decouples layouts from the View containers that manage scrolling and view recycling. FreeFlow makes it really easy to create custom layouts and beautiful transition animations as layouts are changed.",
				"2) FreeFlow is a composition based approach to Android Layouts. As opposed to default Android Layouts, FreeFlow Layouts are swappable at runtime which allows views to their new states smoothly. The fundamental difference here is that FreeFlow prefers Composition over Inheritance which makes the system a lot more adaptable.",
				"3) Freeflow may be considered in 'alpha'. You can help in many ways, by reviewing and making suggestions on api's to actually finding bugs and submitting patches via pull requests.",
				"4) A layout engine for Android that decouples layouts from the View containers that manage scrolling and view recycling. FreeFlow makes it really easy to create custom layouts and beautiful transition animations as layouts are changed.",
				"5) FreeFlow is a composition based approach to Android Layouts. As opposed to default Android Layouts, FreeFlow Layouts are swappable at runtime which allows views to their new states smoothly. The fundamental difference here is that FreeFlow prefers Composition over Inheritance which makes the system a lot more adaptable.",
				"6) Freeflow may be considered in 'alpha'. You can help in many ways, by reviewing and making suggestions on api's to actually finding bugs and submitting patches via pull requests.",
				"7) A layout engine for Android that decouples layouts from the View containers that manage scrolling and view recycling. FreeFlow makes it really easy to create custom layouts and beautiful transition animations as layouts are changed.",
				"8) FreeFlow is a composition based approach to Android Layouts. As opposed to default Android Layouts, FreeFlow Layouts are swappable at runtime which allows views to their new states smoothly. The fundamental difference here is that FreeFlow prefers Composition over Inheritance which makes the system a lot more adaptable.",
				"9) Freeflow may be considered in 'alpha'. You can help in many ways, by reviewing and making suggestions on api's to actually finding bugs and submitting patches via pull requests.",
				"10) A layout engine for Android that decouples layouts from the View containers that manage scrolling and view recycling. FreeFlow makes it really easy to create custom layouts and beautiful transition animations as layouts are changed.",
				"11) FreeFlow is a composition based approach to Android Layouts. As opposed to default Android Layouts, FreeFlow Layouts are swappable at runtime which allows views to their new states smoothly. The fundamental difference here is that FreeFlow prefers Composition over Inheritance which makes the system a lot more adaptable.",
				"12) Freeflow may be considered in 'alpha'. You can help in many ways, by reviewing and making suggestions on api's to actually finding bugs and submitting patches via pull requests."

		};

		layout = new AdaptiveTextHeightLayout();
		layout.initWithStrings(strings);

		
		container.setLayout(layout);
		DataAdapter adapter = new DataAdapter(strings);
		container.setAdapter(adapter);

	}

	class AdaptiveTextHeightLayout extends FreeFlowLayoutBase implements
			FreeFlowLayout {

		private HashMap<Object, FreeFlowItem> proxies = new HashMap<Object, FreeFlowItem>();

		int itemGap = 20;

		private String[] strings;

		public void initWithStrings(String[] strings) {
			this.strings = strings;
		}

		private int[] heights;

		public void measureStrings() {
			heights = new int[strings.length];
			for (int i = 0; i < strings.length; i++) {
				TextPaint p = new TextPaint();

				p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
				p.setTextSize(42);
			
				
				StaticLayout sl = new StaticLayout(strings[i], p, width ,
						Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, true);
				heights[i] = sl.getHeight();
			}
		}


		@Override
		public Map<Object, FreeFlowItem> getItemProxies(int viewPortLeft,
				int viewPortTop) {
			Rect viewport = new Rect(viewPortLeft, viewPortTop, viewPortLeft
					+ width, viewPortTop + height);

			HashMap<Object, FreeFlowItem> ret = new HashMap<Object, FreeFlowItem>();

			Iterator<Entry<Object, FreeFlowItem>> it = proxies.entrySet()
					.iterator();
			while (it.hasNext()) {
				Entry<Object, FreeFlowItem> pairs = it.next();
				FreeFlowItem p = (FreeFlowItem) pairs.getValue();
				if (Rect.intersects(p.frame, viewport)) {
					ret.put(pairs.getKey(), p);
				}
			}
			return ret;
		}

		@Override
		public void setLayoutParams(FreeFlowLayoutParams params) {
			// 
		}

		@Override
		public void prepareLayout() {
			proxies = new HashMap<Object, FreeFlowItem>();
			measureStrings();
			int top = 0;
			for (int i = 0; i < strings.length; i++) {
				FreeFlowItem descriptor = new FreeFlowItem();
				Rect frame = new Rect();
				descriptor.itemSection = 0;
				descriptor.itemIndex = i;
				frame.left = 0;
				frame.top = top;

				top = top + heights[i] + itemGap;

				frame.right = width;
				frame.bottom = frame.top + heights[i];
				descriptor.frame = frame;
				descriptor.data = strings[i];
				proxies.put(descriptor.data, descriptor);
			}
		}

		@Override
		public FreeFlowItem getFreeFlowItemForItem(Object item) {
			return proxies.get(item);
		}

		@Override
		public boolean horizontalScrollEnabled() {
			return false;
		}

		@Override
		public boolean verticalScrollEnabled() {
			return true;
		}

		@Override
		public int getContentWidth() {
			return 0;
		}

		@Override
		public int getContentHeight() {
			int t = 0;
			for (int i = 0; i < heights.length; i++) {
				t = t + heights[i] + itemGap;
			}
			return t;
		}

		@Override
		public FreeFlowItem getItemAt(float x, float y) {
			return ViewUtils.getItemAt(proxies, (int) x, (int) y);
		}

	}

	class DataAdapter implements SectionedAdapter {

		private ArrayList<Section> sections = new ArrayList<Section>();

		private String[] strings;
		public DataAdapter(String[] strings) {
			
			this.strings = strings;
			Section s = new Section();

			s.setSectionTitle("Section ");
			for (int j = 0; j < strings.length; j++) {
				s.addItem(strings[j]);
			}
			sections.add(s);

		}

		@Override
		public long getItemId(int section, int position) {
			return section * 1000 + position;
		}

		@Override
		public View getItemView(int section, int position, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			
			if (convertView != null) {
				// Log.d(TAG, "Convert view not null");
				tv = (TextView) convertView;
			} else {
				tv = new TextView(FFActivity.this);
			}
			tv.setText(strings[position]);

			tv.setFocusable(false);
			tv.setBackgroundColor(0xffcfcfcf);
			

			return tv;
		}

		@Override
		public View getHeaderViewForSection(int section, View convertView,
				ViewGroup parent) {
			TextView tv = null;
			if (convertView != null) {
				tv = (TextView) convertView;
			} else {
				tv = new TextView(FFActivity.this);
			}

			tv.setFocusable(false);
			tv.setBackgroundColor(Color.GRAY);
			tv.setText("section header" + section);

			return tv;
		}

		@Override
		public int getNumberOfSections() {
			return sections.size();
		}

		@Override
		public Section getSection(int index) {
			if (index < sections.size() && index >= 0)
				return sections.get(index);

			return null;
		}

		@Override
		public Class[] getViewTypes() {
			Class[] types = { TextView.class, TextView.class };

			return types;
		}

		@Override
		public Class getViewType(FreeFlowItem proxy) {
			return TextView.class;
		}

		@Override
		public boolean shouldDisplaySectionHeaders() {
			return true;
		}

	}

}
