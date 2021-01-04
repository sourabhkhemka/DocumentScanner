import cv2 as cv
import numpy as np

'''
 Function to map values in range [in_min, in_max] to the range [out_min, out_max]
'''
def map(x, in_min, in_max, out_min, out_max):
	return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min


# global variables for image ,blackPoint, whitePoint
img = None; whitePoint = None; blackPoint = None;


'''
 * High Pass Filter
 * Output of HPF depends on the kernel size provided as input argument
 * Links to the docuementation:
 *  Introduction: https://github.com/sourabhkhemka/DocumentScanner/wiki/Scan:-Introduction
 *  HPF: https://github.com/sourabhkhemka/DocumentScanner/wiki/GCMODE
 *
'''
def highPassFilter(kSize):
	global img
	
	print("applying high pass filter")
	
	if not kSize%2:
		kSize +=1
		
	kernel = np.ones((kSize,kSize),np.float32)/(kSize*kSize)
	
	filtered = cv.filter2D(img,-1,kernel)
	
	filtered = img.astype('float32') - filtered.astype('float32')
	filtered = filtered + 127*np.ones(img.shape, np.uint8)
	
	filtered = filtered.astype('uint8')
	
	img = filtered
	
	# "img" now contains high pass filtered image.

	
	
'''
 * Method to select black point in the image
 *
 * Links to documentation: 
 *  Introduction: https://github.com/sourabhkhemka/DocumentScanner/wiki/Scan:-Introduction
 *  Black Point Select: https://github.com/sourabhkhemka/DocumentScanner/wiki/Black-Point-Select
''' 
def blackPointSelect():
	global img
	
	print("adjusting black point for final output ...")
	
	# refer repository's wiki page for detailed explanation

	img = img.astype('int32')

	img = map(img, blackPoint, 255, 0, 255)

	#if cv.__version__ == '3.4.4':
		#img = img.astype('uint8')

	_, img = cv.threshold(img, 0, 255, cv.THRESH_TOZERO)

	img = img.astype('uint8')
	

'''
 * Method to select whitePoint in the image
 *
 * Links to documentation: 
 *  Introduction: https://github.com/sourabhkhemka/DocumentScanner/wiki/Scan:-Introduction
 *  White Point Select: https://github.com/sourabhkhemka/DocumentScanner/wiki/White-Point-Select
'''
def whitePointSelect():
	global img
	
	print("white point selection running ...")

	# refer repository's wiki page for detailed explanation

	_,img = cv.threshold(img, whitePoint, 255, cv.THRESH_TRUNC)

	img = img.astype('int32')
	img = map(img, 0, whitePoint, 0, 255)
	img = img.astype('uint8')
	
	
'''
 * Method to select black point in the image
 *
 * Links to documentation: 
 *  Introduction: https://github.com/sourabhkhemka/DocumentScanner/wiki/Scan:-Introduction
 *  Black Point Select: https://github.com/sourabhkhemka/DocumentScanner/wiki/Black-Point-Select
 *
''' 
def blackAndWhite():
	global img
	
	# refer repository's wiki page for detailed explanation

	lab = cv.cvtColor(img, cv.COLOR_BGR2LAB)

	(l,a,b) = cv.split(lab)

	img = cv.add( cv.subtract(l,b), cv.subtract(l,a) )



if __name__ == "__main__":

	img = cv.imread("C:/Users/Sourabh Khemka/Desktop/RScan/ML/dataset/data_img_008.jpg")

	# define values for blackPoint and whitePoint
	blackPoint = 66
	whitePoint = 160
	
	# store desired mode of operation as string
	mode = "GCMODE"

	if mode == "GCMODE":
		highPassFilter(kSize = 51)
		whitePoint = 127
		whitePointSelect()
		blackPointSelect()
	elif mode == "RMODE":
		blackPointSelect()
		whitePointSelect()
	elif mode == "SMODE":
		blackPointSelect()
		whitePointSelect()
		blackAndWhite()



print("\ndone.")

cv.imwrite('C:/Users/Sourabh Khemka/Desktop/RScan/ML/op.jpg', img)

cv.imshow("final", cv.resize(img,None,fx=0.125, fy=0.125, interpolation = cv.INTER_CUBIC))
cv.waitKey(0)
