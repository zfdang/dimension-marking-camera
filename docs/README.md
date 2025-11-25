# DimensionCam Project Pages

This directory contains the GitHub Pages website for the DimensionCam project.

## Files

- `index.html` - Main landing page
- `download.html` - Download page with installation guide
- `style.css` - Shared stylesheet
- `dimensioncam.png` - App screenshot/logo

## Viewing Locally

Open `index.html` in your browser to preview the site locally.

## GitHub Pages Setup

1. Go to your GitHub repository settings
2. Navigate to "Pages" section
3. Under "Source", select "Deploy from a branch"
4. Select the `main` branch and `/docs` folder
5. Click "Save"
6. The CNAME file is already configured for your custom domain

Your site will be published at: **https://dimcam.zfdang.com/**

### DNS Configuration

Make sure your DNS is configured with a CNAME record:
- Type: CNAME
- Name: dimcam (or dimcam.zfdang.com)
- Value: zfdang.github.io

## Customization

Feel free to:
- Add more screenshots to the screenshots section
- Update the download links when releases are available
- Add demo videos or GIFs
- Customize colors in `style.css`
