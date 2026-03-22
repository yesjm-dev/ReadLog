/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        logo: ['Gaegu', 'cursive'],
        review: ['Gowun Batang', 'serif'],
      },
    },
  },
  plugins: [],
}
