/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#3F51B5', // Deep Blue/Purple (from implementation plan)
        accent: '#FF4081',  // Pink (from implementation plan)
        // Add more 'Wow' colors here
        'deep-purple': '#673AB7',
        'teal': '#009688',
      },
    },
  },
  plugins: [],
}
