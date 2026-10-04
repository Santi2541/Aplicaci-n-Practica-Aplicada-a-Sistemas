module co.edu.poli.all10prueba {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;

    opens co.edu.poli.all10.controller to javafx.fxml;
    opens co.edu.poli.all10.modelo to javafx.base;
    exports co.edu.poli.all10prueba;
    exports co.edu.poli.all10.modelo;
    
}
