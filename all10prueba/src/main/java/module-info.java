module co.edu.poli.all10prueba {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.poli.all10.controller to javafx.fxml;
    exports co.edu.poli.all10prueba;
}
