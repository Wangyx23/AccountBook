<?php
$db_server = "sophia.cs.hku.hk";
$db_user = "yxwang2";
$db_pwd = "19960201";
$conn = mysqli_connect($db_server, $db_user, $db_pwd, $db_user) or die(mysqli_error());


$userID = (isset($_GET['userID']) ? $_GET['userID'] : "");
$pwd = (isset($_GET['pwd']) ? $_GET['pwd'] : "");

$sql = "SELECT * FROM user_tb WHERE userID='".$userID."'";
$res = mysqli_query($conn, $sql) or die(mysqli_error());
echo '{';
if (mysqli_num_rows($res) > 0) {
	echo '"s":"success"';
	$sql = "UPDATE user_tb SET pwd='".$pwd."' WHERE userID='".$userID."'";
	$res = mysqli_query($conn, $sql) or die(mysqli_error());
}else {echo '"s":"error"';}
echo '}';

mysqli_close($conn);
?>
