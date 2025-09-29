package viz;

import grid.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * GridPanel שממלא תמיד את כל השטח הזמין.
 * עובד היטב בתוך JScrollPane ע"י מעקב אחרי גודל ה־viewport.
 */
public class GridPanel extends JPanel implements Scrollable {

    private final GridMap map;
    private RC start, goal;

    private final Set<RC> queued  = new HashSet<>();
    private final Set<RC> visited = new HashSet<>();
    private List<RC> path = null;

    // גודל בסיס להעדפת גודל – הציור בפועל תמיד מותאם לגודל ה־panel
    private static final int BASE_CELL = 28;

    public GridPanel(GridMap map, RC start, RC goal){
        this.map = map;
        this.start = start;
        this.goal  = goal;
        setOpaque(true);
    }

    // ===== API שה־App משתמש בו =====
    public void clearStates(){
        queued.clear();
        visited.clear();
        path = null;
        repaint();
    }

    public void apply(StepEvent ev){
        if (ev == null) return;
        switch (ev.type) {
            case QUEUE -> { if (ev.cell != null) queued.add(ev.cell); }
            case VISIT -> { if (ev.cell != null) { queued.remove(ev.cell); visited.add(ev.cell); } }
            case UPDATE -> { /* אופציונלי לצבוע אחרת עדכונים */ }
            case DONE -> { path = ev.pathIfDone; }
        }
        repaint();
    }

    public void setStartGoal(RC s, RC g){
        this.start = s; this.goal = g; repaint();
    }

    // ===== ציור =====
    @Override protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cols = map.cols(), rows = map.rows();
        int panelW = getWidth(), panelH = getHeight();

        // גודל תא דינמי שממלא את כל השטח (שמירה על ריבועיות)
        int cell = Math.max(1, Math.min(panelW / Math.max(cols,1), panelH / Math.max(rows,1)));
        int gridW = cell * cols, gridH = cell * rows;
        int offX = (panelW - gridW) / 2, offY = (panelH - gridH) / 2;

        // רקע
        g2.setColor(new Color(0x0a1222));
        g2.fillRect(0, 0, panelW, panelH);

        // טקסט עלויות
        Font font = getFont().deriveFont(Font.PLAIN, Math.max(10f, cell * 0.35f));
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = offX + c * cell, y = offY + r * cell;

                // בסיס התא
                if (map.isWall(r, c)) g2.setColor(new Color(0x1b263b));
                else g2.setColor(new Color(0x0f1b2d));
                g2.fillRect(x, y, cell, cell);

                // מצבים (תור/ביקור)
                RC rc = new RC(r, c);
                if (visited.contains(rc)) {
                    g2.setColor(new Color(0x2e8bc0));
                    g2.fillRect(x, y, cell, cell);
                } else if (queued.contains(rc)) {
                    g2.setColor(new Color(0x4f5d75));
                    g2.fillRect(x, y, cell, cell);
                }

                // טקסט עלות
                if (!map.isWall(r, c) && cell >= 16) {
                    String txt = String.format(java.util.Locale.US, "%.1f", map.getCost(r, c));
                    int tw = fm.stringWidth(txt), th = fm.getAscent();
                    g2.setColor(new Color(255,255,255,170));
                    g2.drawString(txt, x + (cell - tw)/2, y + (cell + th)/2 - 2);
                }

                // קו רשת עדין
                g2.setColor(new Color(255,255,255,20));
                g2.drawRect(x, y, cell, cell);
            }
        }

        // מסלול
        if (path != null && path.size() > 1) {
            g2.setStroke(new BasicStroke(Math.max(2f, cell * 0.12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(new Color(0xffcc00));
            for (int i = 1; i < path.size(); i++) {
                RC a = path.get(i-1), b = path.get(i);
                int ax = offX + a.c * cell + cell/2, ay = offY + a.r * cell + cell/2;
                int bx = offX + b.c * cell + cell/2, by = offY + b.r * cell + cell/2;
                g2.drawLine(ax, ay, bx, by);
            }
        }

        // סימון התחלה/סיום
        drawMarker(g2, start, offX, offY, cell, new Color(0x22c55e)); // ירוק
        drawMarker(g2, goal,  offX, offY, cell, new Color(0xef4444)); // אדום
        g2.dispose();
    }

    private void drawMarker(Graphics2D g2, RC p, int offX, int offY, int cell, Color color){
        if (p == null) return;
        int x = offX + p.c * cell, y = offY + p.r * cell;
        int pad = Math.max(2, (int)Math.round(cell * 0.15));
        int w = Math.max(4, cell - 2*pad);
        int h = w;
        g2.setColor(color);
        g2.fillRoundRect(x + pad, y + pad, w, h, Math.max(4, cell/4), Math.max(4, cell/4));
    }

    // ===== Scrollable – כדי שה־panel יעקוב אחרי ה־viewport של JScrollPane =====
    @Override public Dimension getPreferredSize() {
        return new Dimension(map.cols() * BASE_CELL, map.rows() * BASE_CELL);
    }
    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return true; }
    @Override public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) { return BASE_CELL; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) { return BASE_CELL * 4; }
}
