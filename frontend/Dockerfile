# --- Stage 1: build the React app ---
FROM node:20-alpine AS build

WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm install

COPY . .
RUN npm run build

# --- Stage 2: serve the built static files with Nginx ---
FROM nginx:alpine

# React Router uses client-side routing (BrowserRouter), so any
# unknown path (e.g. a refresh on /dashboard) must fall back to
# index.html instead of a raw 404 - this config handles that.
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]