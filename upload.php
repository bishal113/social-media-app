<?php 
 
 //Constants for database connection
 define('DB_HOST','localhost');
 define('DB_USER','root');
 define('DB_PASS','');
 define('DB_NAME','bishal');
 
 //We will upload files to this folder
 //So one thing don't forget, also create a folder named uploads inside your project folder i.e. MyApi folder
 define('UPLOAD_PATH', 'uploads/');
 
 //connecting to database 
 $conn = new mysqli(DB_HOST,DB_USER,DB_PASS,DB_NAME) or die('Unable to connect');
 
 
 //An array to display the response
 $response = array();
 
 //if the call is an api call 
 if(isset($_GET['apicall'])){
 
 //switching the api call 
 switch($_GET['apicall']){
 
 //if it is an upload call we will upload the image
 case 'upload':
 
 //first confirming that we have the image and tags in the request parameter
 if(isset($_FILES['image_video']['name']) && isset($_POST['title']) && isset($_POST['filetype']) && isset($_POST['id'])&& isset($_POST['address'])){
 
 //uploading file and storing it to database as well 
 try{
	 $id= intval($_POST['id']);
 move_uploaded_file($_FILES['image_video']['tmp_name'], UPLOAD_PATH . $_FILES['image_video']['name']);
 date_default_timezone_set('Asia/Kolkata'); 
 $t = date('d-m-Y H:i:s');
 $stmt = $conn->prepare("INSERT INTO images (image, name, DT, filetype, user_id, address) VALUES (?,?,?,?,?,?)");
 $stmt->bind_param("ssssis", $_FILES['image_video']['name'],$_POST['title'],$t, $_POST['filetype'], $id, $_POST['address'] );
 if($stmt->execute()){
 $response['error'] = false;
 $response['message'] = 'File uploaded successfully';
 }else{
 throw new Exception("Could not upload file");
 }
 }catch(Exception $e){
 $response['error'] = true;
 $response['message'] = 'Could not upload file';
 }
 
 }else{
 $response['error'] = true;
 $response['message'] = "Required params not available";
 }
 
 break;
 
 //in this call we will fetch all the images 
 case 'extract':
 
 //getting server ip for building image url 
 $server_ip = gethostbyname(gethostname());
 
 $_POST['userid']='11';
 
 if(isset($_POST['userid'])){
 
 //query to get images from database
 $stmt = $conn->prepare("SELECT auth.username,auth.profilepic,images.id,images.image,images.name,images.DT,images.filetype,images.agrees,images.address,images.comments FROM auth,images WHERE auth.id=images.user_id ORDER BY images.DT DESC");
 $stmt->execute();
 $stmt->bind_result($username,$profilepic, $id, $image, $name, $time, $filetype, $agrees, $address, $comments);
 
 $images = array();
  
 //fetching all the images from database
 //and pushing it to array 
 while($stmt->fetch()){
 $temp = array();
 $temp['username']=$username;
 if($profilepic!=null){
 $temp['profilepic']= 'http://'.$server_ip.'/androiduploads/'.UPLOAD_PATH.$profilepic;
 }
 else{
	 $temp['profilepic']= null;
 }
 $temp['id'] = $id; 
 $temp['image'] = 'http://'.$server_ip.'/androiduploads/'.UPLOAD_PATH.$image; 
 $temp['name'] = $name; 
 $temp['time']= $time;
 $temp['filetype']= $filetype;
 $temp['agrees']= $agrees;
 $temp['address']= $address;
 $temp['comments']=$comments;
 
 array_push($images, $temp);
 }
 
$st = $conn->prepare("SELECT image_id from likes where user_id=?");
$st->bind_param("i",intval($_POST['userid']));
$st->execute();
$st->bind_result($imageid);

 $likes = array();
 while($st->fetch()){
 array_push($likes,$imageid);
 }
 
 //pushing the array in response 
 $response['error'] = false;
 $response['images'] = $images;
 $response['liked_images']  = $likes;
 
 }else{
 $response['error'] = false;
 $response['message'] = "Required params not available";
 }
 
 
 break; 
 
 default: 
 $response['error'] = true;
 $response['message'] = 'Invalid api call';
 }
 
 }else{
 header("HTTP/1.0 404 Not Found");
 echo "<h1>404 Not Found</h1>";
 echo "The page that you have requested could not be found.";
 exit();
 }
 
 //displaying the response in json 
 header('Content-Type: application/json');
 echo json_encode($response);
 ?>