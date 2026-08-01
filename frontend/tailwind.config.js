/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        panel: "#0f172a", // Midnight
        accent: "#3b82f6", // Electric Blue
        surface: "#1e293b", // Slate
      }
    },
  },
  plugins: [],
}