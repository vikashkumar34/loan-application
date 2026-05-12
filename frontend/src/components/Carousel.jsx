import React, { useState, useEffect } from 'react';

const images = [
  '/images/slide1.jpg',
  '/images/slide2.jpg',
  '/images/slide3.jpg'
];

const overlays = [
  { title: 'Welcome', description: 'Your trusted partner in financial services.' },
  { title: 'Current Offers', description: 'Check out our latest loan offers with competitive interest rates.' },
  { title: 'About Us', description: 'Learn more about our mission to help you achieve your financial goals.' }
];

export default function Carousel() {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentIndex(prevIndex => (prevIndex + 1) % images.length);
    }, 5000); // Change slide every 5 seconds
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="relative w-full h-full">
      {images.map((src, index) => (
        <div
          key={index}
          className={`absolute inset-0 transition-opacity duration-1000 ${index === currentIndex ? 'opacity-100' : 'opacity-0'}`}
        >
          <img src={src} alt={`Slide ${index + 1}`} className="w-full h-full object-cover" />
          <div className="absolute inset-0 bg-black bg-opacity-50 flex flex-col items-center justify-center text-white p-8">
            <h2 className="text-4xl font-bold mb-4">{overlays[index].title}</h2>
            <p className="text-lg text-center">{overlays[index].description}</p>
          </div>
        </div>
      ))}
    </div>
  );
}
