<?php
$db_server = "sophia.cs.hku.hk";
$db_user = "yxwang2";
$db_pwd = "19960201";
$conn = mysqli_connect($db_server, $db_user, $db_pwd, $db_user) or die(mysqli_error());

$action = (isset($_GET['action']) ? $_GET['action'] : "");
$userID = (isset($_GET['userID']) ? $_GET['userID'] : "");
$rate = (isset($_GET['rate']) ? $_GET['rate'] : "");

if ($action == "update") {
	
	
	$sql = "UPDATE account_tb SET rate=".$rate." WHERE userID='".$userID."' AND currency='HKD'";
	echo $sql;
	mysqli_query($conn, $sql);
	$sql = "UPDATE exchange_rate SET rate=".$rate." WHERE userID='".$userID."' AND currency='HKD'";
	echo $sql;
	mysqli_query($conn, $sql);
}
mysqli_close($conn);
?>