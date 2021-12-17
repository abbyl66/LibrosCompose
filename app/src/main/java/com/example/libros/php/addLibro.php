<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDAbigail";

//Conexión a BD
$conexion = mysqli_connect($server, $user, $pass,$bd)
or die("Error en la base de datos");

//generamos la consulta
$titulo = $_GET["titulo"];
$autor = $_GET["autor"];

    $sql = "INSERT INTO Libros (titulo,autor) VALUES ('$titulo', '$autor')";
echo $sql;

mysqli_set_charset($conexion, "utf8");
if (mysqli_query($conexion, $sql)) {
      echo "New record created successfully";
} else {
      echo "Error: " . $sql . "<br>" . mysqli_error($conexion);
}

//desconectamos la base de datos
$close = mysqli_close($conexion)
or die("Error en la base de datos");


?>