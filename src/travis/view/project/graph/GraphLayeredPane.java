package travis.view.project.graph;

import java.awt.BorderLayout;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GraphLayeredPane extends JPanel {

	private static final long serialVersionUID = 2855872910533934574L;

	private final JLayeredPane layeredPane;
	private final GraphPanel graph;
	private final GraphTooltip tooltip;

	public GraphLayeredPane(GraphPanel graph, GraphTooltip tooltip) {
		super(new BorderLayout());
		this.graph = graph;
		this.tooltip = tooltip;

		layeredPane = new JLayeredPane();
		layeredPane.setOpaque(true);

		layeredPane.add(graph, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(tooltip, JLayeredPane.POPUP_LAYER);

		add(layeredPane, BorderLayout.CENTER);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		graph.setBounds(0, 0, width, height);
		tooltip.setBounds(0, 0, width, height);
	}
}
