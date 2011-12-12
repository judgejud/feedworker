package org.feedworker.client.frontend.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPaintContext;
/**
 *
 * @author luca judge
 */
public class canvasCalendar extends PCanvas{
    private final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 13);
    private CalendarNode calendarNode;
    
    public canvasCalendar(ArrayList<String> date, ArrayList<ArrayList<String>> shows) {
        calendarNode = new CalendarNode(date, shows);
        getLayer().addChild(calendarNode);

        setZoomEventHandler(null);
        setPanEventHandler(null);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent arg0) {
                calendarNode.setBounds(getX(), getY(), getWidth() - 1, getHeight() - 1);
                calendarNode.layoutChildren(false);
            }
        });
    }

    class CalendarNode extends PNode {
        int DEFAULT_ANIMATION_MILLIS = 250;
        float FOCUS_SIZE_PERCENT = 0.65f;
        int daysExpanded = 0;
        int weeksExpanded = 0;
        int numDays=7, numWeeks=8;
        
        public CalendarNode(ArrayList<String> dateString, ArrayList<ArrayList<String>> shows) {
            int count = 0;
            for (int week = 0; week < numWeeks; week++) {
                for (int day = 0; day < numDays; day++) {
                    addChild(new DayNode(week, day, dateString.get(count), shows.get(count)));
                    count++;
                }
            }
            addInputEventListener(inputEvent());
        }
        
        private PBasicInputEventHandler inputEvent(){
            return new PBasicInputEventHandler() {
                @Override
                public void mouseReleased(PInputEvent event) {
                    DayNode pickedDay = (DayNode) event.getPickedNode();
                    if (pickedDay.hasWidthFocus && pickedDay.hasHeightFocus)
                        setFocusDay(null, true);
                    else
                        setFocusDay(pickedDay, true);
                }
            };
        }

        public DayNode getDay(int week, int day) {
            return (DayNode) getChild((week * numDays) + day);
        }

        public void setFocusDay(DayNode focusDay, boolean animate) {
            for (int i = 0; i < getChildrenCount(); i++) {
                DayNode each = (DayNode) getChild(i);
                each.hasWidthFocus = false;
                each.hasHeightFocus = false;
            }
            if (focusDay == null) {
                daysExpanded = 0;
                weeksExpanded = 0;
            } else {
                focusDay.hasWidthFocus = true;
                daysExpanded = 1;
                weeksExpanded = 1;

                for (int i = 0; i < numDays; i++)
                    getDay(focusDay.week, i).hasHeightFocus = true;

                for (int i = 0; i < numWeeks; i++)
                    getDay(i, focusDay.day).hasWidthFocus = true;
            }
            layoutChildren(animate);
        }

        protected void layoutChildren(boolean animate) {
            double focusWidth = 0;
            double focusHeight = 0;

            if (daysExpanded != 0 && weeksExpanded != 0) {
                focusWidth = (getWidth() * FOCUS_SIZE_PERCENT) / daysExpanded;
                focusHeight = (getHeight() * FOCUS_SIZE_PERCENT) / weeksExpanded;
            }

            double collapsedWidth = (getWidth() - (focusWidth * daysExpanded))
                / (numDays - daysExpanded);
            double collapsedHeight = (getHeight() - (focusHeight * weeksExpanded))
                / (numWeeks - weeksExpanded);

            double xOffset = 0;
            double yOffset = 0;
            double rowHeight = 0;
            DayNode each = null;

            for (int week = 0; week < numWeeks; week++) {
                for (int day = 0; day < numDays; day++) {
                    each = getDay(week, day);
                    double width = collapsedWidth;
                    double height = collapsedHeight;

                    if (each.hasWidthFocus()) width = focusWidth;
                    if (each.hasHeightFocus()) height = focusHeight;

                    if (animate) {
                        each.animateToBounds(xOffset, yOffset, width,
                            height, DEFAULT_ANIMATION_MILLIS).setStepRate(0);
                    } else {
                        each.setBounds(xOffset, yOffset, width, height);
                    }

                    xOffset += width;
                    rowHeight = height;
                }
                xOffset = 0;
                yOffset += rowHeight;
            }
        }
    }

    class DayNode extends PNode {
        private final int TEXT_X_OFFSET = 1;
        private final int TEXT_Y_OFFSET = 10;

        private boolean hasWidthFocus, hasHeightFocus;
        private ArrayList lines;
        private int week, day;
        private String date;

        public DayNode(int week, int day, String date, ArrayList lines) {
            this.lines = lines;
            this.week = week;
            this.day = day;
            this.date =  "\n" + date;
            setPaint(Color.BLACK);
        }

        public int getWeek() {
            return week;
        }

        public int getDay() {
            return day;
        }

        public boolean hasHeightFocus() {
            return hasHeightFocus;
        }

        public void setHasHeightFocus(boolean hasHeightFocus) {
            this.hasHeightFocus = hasHeightFocus;
        }

        public boolean hasWidthFocus() {
            return hasWidthFocus;
        }

        public void setHasWidthFocus(boolean hasWidthFocus) {
            this.hasWidthFocus = hasWidthFocus;
        }

        @Override
        protected void paint(PPaintContext paintContext) {
            Graphics2D g2 = paintContext.getGraphics();

            g2.setPaint(getPaint());
            g2.draw(getBoundsReference());
            g2.setFont(DEFAULT_FONT);

            float y = (float) getY() + TEXT_Y_OFFSET;
            float x = (float) getX() + TEXT_X_OFFSET;
            paintContext.getGraphics().drawString(date, x, y);

            if (hasWidthFocus && hasHeightFocus) {
                paintContext.pushClip(getBoundsReference());
                for (int i = 0; i < lines.size(); i++) {
                    y += 10;
                    g2.drawString((String)lines.get(i), x, y);
                }
                paintContext.popClip(getBoundsReference());
            }
        }
    }
}