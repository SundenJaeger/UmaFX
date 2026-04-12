package com.rentoki.umafx.controller;

import com.rentoki.umafx.model.TrackHistory;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;

public class TrackHistoryController {
    private final ObservableList<TrackHistory> trackEntities;

    @FXML
    private ListView<TrackHistory> historyListView;

    public TrackHistoryController(ObservableList<TrackHistory> trackEntities) {
        this.trackEntities = trackEntities;
    }

    @FXML
    private void initialize() {
        historyListView.setItems(trackEntities);
        historyListView.setCellFactory(param -> new TrackHistoryListCell());
    }

    private static class TrackHistoryListCell extends ListCell<TrackHistory> {
        private final Parent root;
        private final HistoryRequestCellController controller;

        TrackHistoryListCell() {
            try {
                FXMLLoader loader = new FXMLLoader(TrackHistoryListCell.class.getResource("/com/rentoki/umafx/views/history-request-cell-view.fxml"));
                root = loader.load();
                controller = loader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void updateItem(TrackHistory item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(root);
                controller.setTrackHistory(item);
            }
        }
    }
}