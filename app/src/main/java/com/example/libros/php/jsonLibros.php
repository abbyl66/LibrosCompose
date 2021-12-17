
<?php

$server = "localhost";
$user = "root";
$pass = "clave";
$bd = "BDAbigail";

//Conexión a la BD
$conexion = mysqli_connect($server, $user, $pass,$bd)
or die("Error en la base de datos");

$sql = "SELECT * FROM Libros";
mysqli_set_charset($conexion, "utf8");

if(!$result = mysqli_query($conexion, $sql)) die();

$libros = array();

while($row = mysqli_fetch_array($result))
{
    $titulo=$row['titulo'];
    $autor=$row['autor'];


    $libros[] = array('titulo'=> $titulo, 'autor'=> $autor);

}

$close = mysqli_close($conexion)
or die("Error en la base de datos");

$json_string = json_encode($libros);
echo $json_string;
?>