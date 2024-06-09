import javax.swing.*;

public class SortPopup extends DatabaseGUI{
    public JPanel sortPanel;
    private JButton applyButton;
    private JButton cancelButton;
    private JComboBox<String> cbScore;
    private JComboBox<String> cbStatus;

    // Interface for callback
    public interface SortListener {
        void onSort(String scoreSortOption, String statusSortOption);
    }

    private SortListener sortListener;

    public void setSortListener(SortListener listener) {
        this.sortListener = listener;
    }

    public SortPopup() {
        cbScore.addItem("-");
        cbScore.addItem("Descending");
        cbScore.addItem("Ascending");

        cbStatus.addItem("-");
        cbStatus.addItem("Plan to watch");
        cbStatus.addItem("Watching");
        cbStatus.addItem("Finished watching");

        applyButton.addActionListener(e -> {
            String scoreSortOption = (String) cbScore.getSelectedItem();
            String statusSortOption = (String) cbStatus.getSelectedItem();
            if (sortListener != null) {
                sortListener.onSort(scoreSortOption, statusSortOption);
            }
            closePopUp();
        });

        cancelButton.addActionListener(e -> {
            closePopUp();
        });

    }

    private void closePopUp() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(sortPanel);
        frame.dispose();
    }
}
