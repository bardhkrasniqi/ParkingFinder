<?php
header("Content-Type: application/json; charset=UTF-8");
require 'init.php';

$query = $conn->prepare("SELECT * FROM zones WHERE id_zone in (SELECT p.id_zone FROM parking p)");
$query->execute();
$result = $query->get_result();
if ($result->num_rows < 1) exit(json_encode(array('success' => false, 'data' => "Receiving data failed! Try again")));
$data = array();
while ($row = $result->fetch_assoc()) {
    $zone_id = $row['id_zone'];
    $zone_name = $row['zone_name'];
    array_push($data, array('id_zone' => $zone_id, 'zone_name' => $zone_name));
}

echo json_encode(array('success' => true, "data" => $data));

$query->close();
$conn->close();

?>