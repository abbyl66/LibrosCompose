<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDAbigail";

//Conexión a BD

$conexion = mysqli_connect($server, $user, $pass,$bd)
or die("Error en la base de datos");

//Asociamos atributos a métodos get

$titulo = $_GET["titulo"];

    $sql = "DELETE FROM Libros WHERE titulo= '$titulo'";
echo $sql;

mysqli_set_charset($conexion, "utf8");
if (mysqli_query($conexion, $sql)) {
      echo "'New record created successfully";
} else {
      echo "Error: " . $sql . "<br>" . mysqli_error($conexion);
}

$close = mysqli_close($conexion)
or die("Error en la base de datos");


?>