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
          emerald: "#064E3B",
          "emerald-dark": "#032C22",
          "emerald-light": "#0A5C44",
          "emerald-accent": "#0E7C5C",
          maroon: "#064E3B",
          "maroon-dark": "#032C22",
          rose: "#0A5C44",
          "rose-dark": "#064E3B",
          "rose-light": "#ECFDF5",
          ruby: "#0A5C44",
          gold: "#D4AF37",
          "gold-light": "#F3E5AB",
          "gold-dark": "#AA820A",
          silk: "#FAF8F5",
          cream: "#F4EFEA",
          charcoal: "#141E1A",
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
