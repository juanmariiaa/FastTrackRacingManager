module org.juanmariiaa {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.juanmariiaa to javafx.fxml;
    exports org.juanmariiaa;
}
