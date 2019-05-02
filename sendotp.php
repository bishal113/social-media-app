

<?php

	//Your authentication key
	$authKey = "abgbsaxhbsadbasbhdjhsajhd";

	//Multiple mobiles numbers separated by comma
	$mobileNumber = $_POST['phno'];

	//Sender ID,While using route4 sender id should be 6 characters long.
	$senderId = "Team_Swachh";

	//Your message to send, Add URL encoding here.
	$message = urlencode("Your One Time Password is "+$_POST['otp']);

	//Define route 
	$route = "default";
	//Prepare you post parameters
	$postData = array(
	    'authkey' => $authKey,
	    'mobiles' => $mobileNumber,
	    'message' => $message,
	    'sender' => $senderId,
	    'route' => $route
	);

	//API URL
	$url="http://control.msg91.com/api/sendhttp.php";

	// init the resource
	$ch = curl_init();
	curl_setopt_array($ch, array(
	    CURLOPT_URL => $url,
	    CURLOPT_RETURNTRANSFER => true,
	    CURLOPT_POST => true,
	    CURLOPT_POSTFIELDS => $postData
	    //,CURLOPT_FOLLOWLOCATION => true
	));


	//Ignore SSL certificate verification
	curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);


	//get response
	$output = curl_exec($ch);
	
	$response = array();

	//Print error if any
	if(curl_errno($ch))
	{ 
	    //curl_error($ch);
            $response['error'] = true;
            $response['message'] = curl_error($ch);
	}else{
            $response['error'] = false;
            $response['message'] = "Successfully sent the otp";
        }

	curl_close($ch);

?>


