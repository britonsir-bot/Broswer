#!/data/data/com.termux/files/usr/bin/bash
set -Eeuo pipefail
mkdir -p "$HOME/.local/bin" "$HOME/.local/share/applications"
cat > "$HOME/.local/bin/aurexis-browser2" <<'EOF'
#!/data/data/com.termux/files/usr/bin/bash
am start -n com.aurexis.browser/.MainActivity
EOF
chmod 755 "$HOME/.local/bin/aurexis-browser2"
cat > "$HOME/.local/share/applications/aurexis-browser2.desktop" <<EOF
[Desktop Entry]
Type=Application
Name=AUREXIS Browser 2
Comment=Mozilla Gecko-based Android browser
Exec=$HOME/.local/bin/aurexis-browser2
Icon=web-browser
Terminal=false
Categories=Network;WebBrowser;
EOF
chmod 644 "$HOME/.local/share/applications/aurexis-browser2.desktop"
echo "AUREXIS Browser 2 launcher installed."
