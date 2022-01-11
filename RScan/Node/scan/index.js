const Jimp = require('jimp')
const cv = require('@techstark/opencv-js')

const anchor = new cv.Point(-1, -1)

const readImg = async (url) => {
  let err
  const img = await Jimp.read(url).catch(err)
  if (err) throw err
  return cv.matFromImageData(img.bitmap)
}

const highPassFilter = (src, kSize) => {
  const dst = src.clone()
  if (kSize % 2 === 0) kSize++
  let kernel = new cv.Mat.ones(kSize, kSize, cv.CV_32FC1)
  kernel = kernel.mul(kernel, 1 / Math.pow(kSize, 2))
  cv.filter2D(src, dst, src.type(), kernel, anchor, -1)
  dst.convertTo(dst, cv.CV_32FC3)
  src.convertTo(src, cv.CV_32FC3)
  cv.subtract(src, dst, dst)
  let scalar = new cv.Scalar(127, 127, 127)
  let newKernel = new cv.Mat(src.rows, src.cols, cv.CV_32FC3, scalar)
  cv.cvtColor(dst, dst, cv.COLOR_RGBA2RGB)
  cv.add(dst, newKernel, dst)
  dst.convertTo(dst, cv.CV_8UC3)
  return dst
}

const whitePointSelect = (src, whitePoint = 127) => {
  const dst = new cv.Mat()
  whitePoint = whitePoint > 255 ? 255 : whitePoint < 0 ? 0 : whitePoint
  cv.threshold(src, dst, whitePoint, 255, cv.THRESH_TRUNC)
  return dst
} 

const blackPointSelect = (src, blackPoint = 66) => {
  let dst = new cv.Mat()
  whitePoint = blackPoint > 254 ? 254 : blackPoint < 0 ? 0 : blackPoint
  let scalar = new cv.Scalar(blackPoint, blackPoint, blackPoint, blackPoint)
  let kernel = new cv.Mat(src.rows, src.cols, src.type(), scalar)
  cv.subtract(src, kernel, dst)
  dst = dst.mul(dst, 255 / (255 - blackPoint))
  return dst
} 

const blackAndWhite = (src) => {
  const dst = src.clone()
  const lab = new cv.MatVector()
  let subA = new cv.Mat()
  let subB = new cv.Mat()
  cv.cvtColor(dst, dst, cv.COLOR_BGR2Lab)
  cv.split(dst, lab)
  cv.subtract(lab.get(0), lab.get(1), subA)
  cv.subtract(lab.get(0), lab.get(2), subB)
  cv.add(subA, subB, dst)
  lab.delete()
  subA.delete()
  subB.delete()
  return dst
}

const scanImage = (src, mode, blackPoint, whitePoint, kSize = 51) => {
  let dst = src.clone()
  if (mode === 'rmode') {
    dst = blackPointSelect(dst, blackPoint)
    dst = whitePointSelect(dst, whitePoint)
    return dst
  }
  if (mode === 'smode') {
    dst = blackPointSelect(src, blackPoint)
    dst = whitePointSelect(dst, whitePoint)
    dst = blackAndWhite(dst)
    return dst
  }
  if (mode === 'gcmode') {
    dst = highPassFilter(dst, kSize)
    dst = whitePointSelect(dst, whitePoint)
    dst = blackPointSelect(dst, blackPoint)
    return dst
  }
}

module.exports = { scanImage }
