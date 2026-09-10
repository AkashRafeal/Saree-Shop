/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          emerald: "#062E28",
          "emerald-dark": "#031D19",
          "emerald-light": "#0A4D40",
          "emerald-rich": "#0F594A",
          "emerald-tint": "#EAF4F1",
          gold: "#D4AF37",
          "gold-light": "#F3E5AB",
          "gold-dark": "#AA820A",
          ruby: "#0A4D40",
          primary: "#0A4D40",
          accent: "#D4AF37",
          silk: "#FAF8F5",
          cream: "#F4EFE6",
          charcoal: "#1A1A1A",
        },
      },
      fontFamily: {
        serif: ['"Playfair Display"', 'Georgia', 'serif'],
        sans: ['"Plus Jakarta Sans"', 'Inter', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
